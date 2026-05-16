package com.styenvy.egtools;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;


import java.util.List;
import java.util.Locale;

/**
 * Paint Brush:
 * - Stores paint color and remaining uses
 * - Left-click paints blocks (no breaking)
 * - Combine with paint bucket in crafting grid to charge
 * - Shows enchanted glint when charged with paint
 */
@EventBusSubscriber(modid = EgTools.MODID)
public class PaintBrushItem extends Item {
    private static final String LEGACY_PAINT_COLOR = "PaintColor";
    private static final String LEGACY_PAINT_USES = "PaintUses";
    public static final int MAX_PAINT_USES = PaintBrushContents.MAX_USES;

    public PaintBrushItem(Properties props) {
        super(props.stacksTo(1));
    }

    /** Enchanted glint when charged with paint */
    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return getPaintUses(stack) > 0;
    }

    /** Right-click is unused */
    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext ctx) {
        return InteractionResult.PASS;
    }

    /** Tooltip */
    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        int uses = getPaintUses(stack);
        if (uses > 0) {
            DyeColor color = getPaintColor(stack);
            if (color != null) {
                tooltip.add(Component.translatable("item.egtools.paint_brush.tooltip.paint", formatColorName(color)).withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("item.egtools.paint_brush.tooltip.uses", uses, MAX_PAINT_USES).withStyle(ChatFormatting.GRAY));
            }
        } else {
            tooltip.add(Component.translatable("item.egtools.paint_brush.tooltip.empty").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("item.egtools.paint_brush.tooltip.charge").withStyle(ChatFormatting.GRAY));
        }
        tooltip.add(Component.translatable("item.egtools.paint_brush.tooltip.use").withStyle(ChatFormatting.DARK_GRAY));
    }

    /* =========================
       Left-click paint behavior
       ========================= */
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock e) {
        Player player = e.getEntity();
        ItemStack brushStack = player.getMainHandItem();
        if (!(brushStack.getItem() instanceof PaintBrushItem)) {
            return;
        }

        e.setCanceled(true);
        if (e.getAction() != PlayerInteractEvent.LeftClickBlock.Action.START) {
            return;
        }
        if (player.level().isClientSide) return;

        int uses = getPaintUses(brushStack);
        if (uses <= 0) {
            player.displayClientMessage(Component.translatable("message.egtools.paint_brush.no_paint").withStyle(ChatFormatting.RED), true);
            return;
        }

        DyeColor color = getPaintColor(brushStack);
        if (color == null) return;

        Level level = player.level();

        // Normalize bed foot -> head before painting.
        BlockPos pos = normalizeToBedHead(level, e.getPos());

        // Now check paintability/paint using the normalized position
        if (!PaintVariantRegistry.isPaintable(level.getBlockState(pos).getBlock())) {
            player.displayClientMessage(Component.translatable("message.egtools.paint_brush.not_paintable").withStyle(ChatFormatting.RED), true);
            return;
        }

        boolean painted = PaintVariantRegistry.paint(level, pos, color);
        if (!painted) {
            player.displayClientMessage(Component.translatable("message.egtools.paint_brush.same_color").withStyle(ChatFormatting.YELLOW), true);
            return;
        }

        if (!player.getAbilities().instabuild) {
            setPaint(brushStack, color, uses - 1);
        }

        level.playSound(null, pos, SoundEvents.BRUSH_SAND_COMPLETED, SoundSource.PLAYERS, 0.8f, 1.1f);
        player.swing(e.getHand(), true);
    }

    /* =========================
       Paint charging methods
       ========================= */

    /**
     * Charges the brush with paint from a bucket.
     * Called from crafting recipe handler.
     */
    public static void chargeBrush(ItemStack brush, DyeColor color, int addUses) {
        if (!(brush.getItem() instanceof PaintBrushItem)) return;

        DyeColor currentColor = getPaintColor(brush);
        int currentUses = getPaintUses(brush);

        int uses = currentColor == color ? currentUses + addUses : addUses;
        setPaint(brush, color, uses);
    }

    /* =========================
       Data storage helpers
       ========================= */

    @Nullable
    private static PaintBrushContents getPaintContents(ItemStack stack) {
        PaintBrushContents contents = stack.get(EgToolsDataComponents.PAINT_BRUSH_CONTENTS.get());
        return contents != null ? contents : getLegacyPaintContents(stack);
    }

    private static DyeColor getPaintColor(ItemStack stack) {
        PaintBrushContents contents = getPaintContents(stack);
        return contents != null ? contents.color() : null;
    }

    private static int getPaintUses(ItemStack stack) {
        PaintBrushContents contents = getPaintContents(stack);
        return contents != null ? contents.uses() : 0;
    }

    private static void setPaint(ItemStack stack, DyeColor color, int uses) {
        removeLegacyPaintData(stack);
        if (uses <= 0) {
            clearPaint(stack);
            return;
        }

        stack.set(EgToolsDataComponents.PAINT_BRUSH_CONTENTS.get(), PaintBrushContents.of(color, uses));
    }

    @Nullable
    private static PaintBrushContents getLegacyPaintContents(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return null;
        }

        CompoundTag tag = data.copyTag();
        if (!tag.contains(LEGACY_PAINT_COLOR, Tag.TAG_STRING) || !tag.contains(LEGACY_PAINT_USES, Tag.TAG_INT)) {
            return null;
        }

        DyeColor color = DyeColor.byName(tag.getString(LEGACY_PAINT_COLOR).toLowerCase(Locale.ROOT), null);
        int uses = tag.getInt(LEGACY_PAINT_USES);
        return color != null && uses > 0 ? PaintBrushContents.of(color, uses) : null;
    }

    private static void removeLegacyPaintData(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return;
        }

        CompoundTag tag = data.copyTag();
        tag.remove(LEGACY_PAINT_COLOR);
        tag.remove(LEGACY_PAINT_USES);
        if (tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    private static void clearPaint(ItemStack stack) {
        removeLegacyPaintData(stack);
        stack.remove(EgToolsDataComponents.PAINT_BRUSH_CONTENTS.get());
    }

    private static String formatColorName(DyeColor color) {
        String name = color.getName().replace('_', ' ');
        String[] words = name.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!result.isEmpty()) result.append(" ");
            result.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1));
        }
        return result.toString();
    }

    /** If the target is a bed FOOT, move pos to the HEAD so painting works consistently. */
    private static BlockPos normalizeToBedHead(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof BedBlock) {
            BedPart part = state.getValue(BedBlock.PART);
            if (part == BedPart.FOOT) {
                return pos.relative(state.getValue(BedBlock.FACING));
            }
        }
        return pos;
    }
}

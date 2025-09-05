package com.styenvy.egtools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
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


import java.util.List;

/**
 * Paint Brush:
 * - Stores paint color and remaining uses
 * - Left-click paints blocks (no breaking)
 * - Combine with paint bucket in crafting grid to charge
 * - Shows enchanted glint when charged with paint
 */
@EventBusSubscriber(modid = EgTools.MODID)
public class PaintBrushItem extends Item {
    private static final String NBT_PAINT_COLOR = "PaintColor";
    private static final String NBT_PAINT_USES = "PaintUses";
    public static final int MAX_PAINT_USES = 128; // 4 buckets worth

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
                tooltip.add(Component.literal("§7Paint: " + formatColorName(color)));
                tooltip.add(Component.literal("§7Uses: " + uses + "/" + MAX_PAINT_USES));
            }
        } else {
            tooltip.add(Component.literal("§7No paint loaded"));
            tooltip.add(Component.literal("§7Combine with paint bucket to charge"));
        }
        tooltip.add(Component.literal("§8Left-click or Shift+Left-click to paint blocks"));
    }

    /* =========================
       Left-click paint behavior
       ========================= */
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock e) {
        Player player = e.getEntity();
        ItemStack heldMain = player.getMainHandItem();
        ItemStack heldOff = player.getOffhandItem();

        ItemStack brushStack = ItemStack.EMPTY;
        InteractionHand brushHand = null;

        if (heldMain.getItem() instanceof PaintBrushItem) {
            brushStack = heldMain;
            brushHand = InteractionHand.MAIN_HAND;
        } else if (heldOff.getItem() instanceof PaintBrushItem) {
            brushStack = heldOff;
            brushHand = InteractionHand.OFF_HAND;
        } else {
            return;
        }

        e.setCanceled(true);
        if (player.level().isClientSide) return;

        int uses = getPaintUses(brushStack);
        if (uses <= 0) {
            player.displayClientMessage(Component.literal("§cBrush has no paint!"), true);
            return;
        }

        DyeColor color = getPaintColor(brushStack);
        if (color == null) return;

        Level level = player.level();

        // 🔑 Normalize bed FOOT -> HEAD before painting
        BlockPos pos = normalizeToBedHead(level, e.getPos());

        // Now check paintability/paint using the normalized position
        if (!PaintVariantRegistry.isPaintable(level.getBlockState(pos).getBlock())) {
            player.displayClientMessage(Component.literal("§cThis block cannot be painted"), true);
            return;
        }

        boolean painted = PaintVariantRegistry.paint(level, pos, color);
        if (!painted) {
            player.displayClientMessage(Component.literal("§eBlock is already this color"), true);
            return;
        }

        if (!player.getAbilities().instabuild) {
            setPaintUses(brushStack, uses - 1);
            if (uses - 1 <= 0) {
                clearPaint(brushStack);
            }
        }

        level.playSound(null, pos, SoundEvents.BRUSH_SAND_COMPLETED, SoundSource.PLAYERS, 0.8f, 1.1f);
        player.swing(brushHand, true);
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

        // If different color, replace completely
        if (currentColor != color && currentColor != null) {
            setPaintColor(brush, color);
            setPaintUses(brush, Math.min(addUses, MAX_PAINT_USES));
        } else {
            // Same color or empty, add uses
            setPaintColor(brush, color);
            setPaintUses(brush, Math.min(currentUses + addUses, MAX_PAINT_USES));
        }
    }

    /* =========================
       Data storage helpers
       ========================= */

    private static DyeColor getPaintColor(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        CompoundTag tag = data.copyTag();
        if (!tag.contains(NBT_PAINT_COLOR)) return null;
        String colorName = tag.getString(NBT_PAINT_COLOR);
        try {
            return DyeColor.valueOf(colorName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static void setPaintColor(ItemStack stack, DyeColor color) {
        CompoundTag tag;
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) tag = new CompoundTag();
        else tag = data.copyTag();
        tag.putString(NBT_PAINT_COLOR, color.name());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static int getPaintUses(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return 0;
        CompoundTag tag = data.copyTag();
        return tag.getInt(NBT_PAINT_USES);
    }

    private static void setPaintUses(ItemStack stack, int uses) {
        CompoundTag tag;
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) tag = new CompoundTag();
        else tag = data.copyTag();
        tag.putInt(NBT_PAINT_USES, Math.max(0, uses));
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static void clearPaint(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            CompoundTag tag = data.copyTag();
            tag.remove(NBT_PAINT_COLOR);
            tag.remove(NBT_PAINT_USES);
            if (tag.isEmpty()) {
                stack.remove(DataComponents.CUSTOM_DATA);
            } else {
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
        }
    }

    private static String formatColorName(DyeColor color) {
        String name = color.name().toLowerCase().replace('_', ' ');
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
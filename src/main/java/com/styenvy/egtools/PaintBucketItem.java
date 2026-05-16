package com.styenvy.egtools;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PaintBucketItem extends Item {
    public static final int MAX_PAINTS = 32;
    private static final String LEGACY_REMAINING = "egtools_paints_remaining";

    private final DyeColor color;

    public PaintBucketItem(Properties props, DyeColor color) {
        super(props.stacksTo(64)); // stackable; no durability here
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.egtools.paint_bucket.tooltip").withStyle(ChatFormatting.GRAY));
        int remaining = getRemaining(stack);
        tooltip.add(Component.translatable("item.egtools.paint_bucket.remaining", remaining, MAX_PAINTS).withStyle(ChatFormatting.GRAY));
    }

    /**
     * Consume exactly one paint. On last use, shrink stack and give Empty Paint Bucket.
     */
    public static void consumeOnePaint(ItemStack stack, Player player) {
        if (player != null && player.getAbilities().instabuild) {
            return; // creative
        }

        int remaining = getRemaining(stack);
        if (remaining <= 0) {
            // Already empty: convert now (safety)
            convertOneToEmpty(stack, player);
            return;
        }

        remaining -= 1;
        if (remaining > 0) {
            setRemaining(stack, remaining);
        } else {
            // This was the last use; convert one to empty.
            convertOneToEmpty(stack, player);
        }
    }

    /* ------------------------- internal helpers ------------------------- */

    private static void convertOneToEmpty(ItemStack stack, Player player) {
        // Remove one used bucket
        stack.shrink(1);
        // Give back one empty bucket to the player (inventory or drop)
        if (player != null) {
            ItemStack empty = EgToolsItems.EMPTY_PAINT_BUCKET.get().getDefaultInstance();
            if (!player.addItem(empty)) {
                player.drop(empty, true);
            }
        }
    }

    public static int getRemaining(ItemStack stack) {
        Integer remaining = stack.get(EgToolsDataComponents.PAINT_BUCKET_REMAINING.get());
        if (remaining != null) {
            return remaining;
        }

        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return MAX_PAINTS;
        CompoundTag tag = data.copyTag();
        return tag.contains(LEGACY_REMAINING, Tag.TAG_INT) ? tag.getInt(LEGACY_REMAINING) : MAX_PAINTS;
    }

    private static void setRemaining(ItemStack stack, int value) {
        removeLegacyRemaining(stack);
        int remaining = Math.max(0, Math.min(MAX_PAINTS, value));
        if (remaining >= MAX_PAINTS) {
            stack.remove(EgToolsDataComponents.PAINT_BUCKET_REMAINING.get());
        } else {
            stack.set(EgToolsDataComponents.PAINT_BUCKET_REMAINING.get(), remaining);
        }
    }

    private static void removeLegacyRemaining(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return;
        }

        CompoundTag tag = data.copyTag();
        tag.remove(LEGACY_REMAINING);
        if (tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }
}

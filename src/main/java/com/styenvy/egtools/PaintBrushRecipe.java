package com.styenvy.egtools;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Custom shapeless recipe for charging paint brushes.
 * Combines PaintBrush + PaintBucket = Charged PaintBrush
 * Returns empty bucket to crafting grid.
 */
public class PaintBrushRecipe extends CustomRecipe {

    public PaintBrushRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        ItemStack brush = ItemStack.EMPTY;
        ItemStack bucket = ItemStack.EMPTY;
        int itemCount = 0;

        // Find exactly one brush and one paint bucket
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            itemCount++;
            if (stack.getItem() instanceof PaintBrushItem) {
                if (!brush.isEmpty()) return false; // Multiple brushes
                brush = stack;
            } else if (stack.getItem() instanceof PaintBucketItem) {
                if (!bucket.isEmpty()) return false; // Multiple buckets
                bucket = stack;
            } else {
                return false; // Unknown item
            }
        }

        // Must have exactly one brush and one bucket
        return itemCount == 2 && !brush.isEmpty() && !bucket.isEmpty();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input, HolderLookup.@NotNull Provider registries) {
        ItemStack brush = ItemStack.EMPTY;
        ItemStack bucket = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof PaintBrushItem) {
                brush = stack.copy();
            } else if (stack.getItem() instanceof PaintBucketItem) {
                bucket = stack;
            }
        }

        if (brush.isEmpty() || bucket.isEmpty()) return ItemStack.EMPTY;

        // Charge the brush with paint
        PaintBucketItem bucketItem = (PaintBucketItem) bucket.getItem();
        PaintBrushItem.chargeBrush(brush, bucketItem.getColor(), PaintBucketItem.MAX_PAINTS);

        return brush;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof PaintBucketItem) {
                // Return an empty paint bucket
                remaining.set(i, EgToolsItems.EMPTY_PAINT_BUCKET.get().getDefaultInstance());
            }
        }

        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EgToolsRecipeSerializers.PAINT_BRUSH_RECIPE.get();
    }
}
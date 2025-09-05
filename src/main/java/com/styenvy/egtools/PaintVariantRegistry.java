package com.styenvy.egtools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * Central repainting helper for vanilla colored blocks.
 * Call {@link #paint(Level, BlockPos, DyeColor)} from an item/ability.
 * Scope (initial):
 * - Beds (repaints both parts)
 * - Candles and Candle Cakes (keeps candle count & lit)
 * - Banners (standing & wall) (keeps facing/rotation)
 * - Wool, Carpets
 * - Terracotta (stained)
 * - Stained Glass, Stained Glass Panes
 * - Concrete, Concrete Powder
 * - Glazed Terracotta
 * Notes:
 * - Where possible, common blockstate properties (e.g., FACING, ROTATION, WATERLOGGED, LIT, etc.) are preserved.
 * - Banner patterns are not preserved here (API churn across versions); this simply swaps the block type.
 */
public final class PaintVariantRegistry {
    private PaintVariantRegistry() {}

    /**
     * Checks if a block can be painted at all.
     * Returns true if the block is any paintable type.
     */
    public static boolean isPaintable(Block block) {
        return block instanceof BedBlock ||
                block instanceof CandleBlock ||
                block instanceof CandleCakeBlock ||
                block instanceof BannerBlock ||
                block instanceof WallBannerBlock ||
                isAnyWool(block) ||
                isAnyCarpet(block) ||
                isAnyTerracotta(block) ||
                isAnyGlazedTerracotta(block) ||
                isAnyStainedGlass(block) ||
                isAnyStainedGlassPane(block) ||
                isAnyConcrete(block) ||
                isAnyConcretePowder(block) ||
                isAnyShulkerBox(block);

    }

    /**
     * Attempts to recolor the block at {@code pos} to the given {@code color}.
     * Returns true if a repaint happened.
     */
    public static boolean paint(Level level, BlockPos pos, DyeColor color) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        // ---- Beds (both parts) ----
        if (block instanceof BedBlock) {
            Block newBed = bedByColor(color);
            if (newBed == null || newBed == block) return false;

            // Copy and set this half
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            BedPart part = state.getValue(BedBlock.PART);

            BlockState thisNew = copyCommonProperties(state, newBed.defaultBlockState())
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, facing)
                    .setValue(BedBlock.PART, part);

            level.setBlock(pos, thisNew, 11);

            // Compute and set the other half
            BlockPos otherPos = (part == BedPart.HEAD) ? pos.relative(facing.getOpposite()) : pos.relative(facing);
            BlockState otherState = level.getBlockState(otherPos);
            if (otherState.getBlock() instanceof BedBlock) {
                BedPart otherPart = (part == BedPart.HEAD) ? BedPart.FOOT : BedPart.HEAD;
                BlockState otherNew = copyCommonProperties(otherState, newBed.defaultBlockState())
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, facing)
                        .setValue(BedBlock.PART, otherPart);
                level.setBlock(otherPos, otherNew, 11);
            }
            return true;
        }

        // ---- Candles ----
        if (block instanceof CandleBlock) {
            Block newCandle = candleByColor(color);
            if (newCandle == null || newCandle == block) return false;

            BlockState newState = copyCommonProperties(state, newCandle.defaultBlockState());
            // preserve candle count if property exists on both
            if (state.hasProperty(CandleBlock.CANDLES) && newState.hasProperty(CandleBlock.CANDLES)) {
                newState = newState.setValue(CandleBlock.CANDLES, state.getValue(CandleBlock.CANDLES));
            }
            // preserve lit
            if (state.hasProperty(BlockStateProperties.LIT) && newState.hasProperty(BlockStateProperties.LIT)) {
                newState = newState.setValue(BlockStateProperties.LIT, state.getValue(BlockStateProperties.LIT));
            }
            level.setBlock(pos, newState, 11);
            return true;
        }

        // ---- Candle Cakes ----
        if (block instanceof CandleCakeBlock) {
            Block newCake = candleCakeByColor(color);
            if (newCake == null || newCake == block) return false;

            BlockState newState = copyCommonProperties(state, newCake.defaultBlockState());
            // preserve lit if present
            if (state.hasProperty(BlockStateProperties.LIT) && newState.hasProperty(BlockStateProperties.LIT)) {
                newState = newState.setValue(BlockStateProperties.LIT, state.getValue(BlockStateProperties.LIT));
            }
            level.setBlock(pos, newState, 11);
            return true;
        }

        // ---- Shulker Boxes ----
        if (isAnyShulkerBox(block)) {
            Block target = shulkerByColor(color);
            if (target == null || target == block) return false;

            // capture current BE (items + name), clear to prevent drops
            var be = level.getBlockEntity(pos);
            net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity old =
                    (be instanceof net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity s) ? s : null;

            net.minecraft.network.chat.Component customName = null;
            net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack> saved = null;
            if (old != null) {
                customName = old.getCustomName();
                int size = old.getContainerSize();
                saved = net.minecraft.core.NonNullList.withSize(size, net.minecraft.world.item.ItemStack.EMPTY);
                for (int i = 0; i < size; i++) saved.set(i, old.getItem(i).copy());
                old.clearContent(); // avoid drops on replacement
            }

            BlockState newState = copyCommonProperties(state, target.defaultBlockState());
            if (state.hasProperty(BlockStateProperties.FACING) && newState.hasProperty(BlockStateProperties.FACING)) {
                newState = newState.setValue(BlockStateProperties.FACING, state.getValue(BlockStateProperties.FACING));
            }
            level.setBlock(pos, newState, 11);

            // restore into the new BE
            var beNew = level.getBlockEntity(pos);
            if (beNew instanceof net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity shulkerNew) {

                // 1) restore name (prefer direct API; else fallback writes NBT)
                if (customName != null) {
                    boolean named = false;
                    try {
                        var m = shulkerNew.getClass().getMethod("setCustomName", net.minecraft.network.chat.Component.class);
                        m.invoke(shulkerNew, customName);
                        named = true;
                    } catch (Throwable ignore) {
                        // fallback: load only the CustomName but this can reset inventory → we'll reapply items after
                        var tag = new net.minecraft.nbt.CompoundTag();
                        tag.putString(
                                "CustomName",
                                net.minecraft.network.chat.Component.Serializer.toJson(customName, level.registryAccess())
                        );
                        shulkerNew.loadWithComponents(tag, level.registryAccess());
                        named = true;
                    }

                    shulkerNew.setChanged();
                }

                // 2) (re)apply saved items LAST so they survive any name-write fallback
                if (saved != null) {
                    int size = Math.min(saved.size(), shulkerNew.getContainerSize());
                    for (int i = 0; i < size; i++) {
                        shulkerNew.setItem(i, saved.get(i));
                    }
                }

                shulkerNew.setChanged();
                level.sendBlockUpdated(pos, newState, newState, 3);
            }
            return true;
        }

        // ---- Banners (standing) ----
        if (block instanceof BannerBlock) {
            Block newBanner = standingBannerByColor(color);
            if (newBanner == null || newBanner == block) return false;

            BlockState newState = copyCommonProperties(state, newBanner.defaultBlockState());
            // preserve rotation (0..15) if present
            if (state.hasProperty(BlockStateProperties.ROTATION_16) && newState.hasProperty(BlockStateProperties.ROTATION_16)) {
                newState = newState.setValue(BlockStateProperties.ROTATION_16, state.getValue(BlockStateProperties.ROTATION_16));
            }
            level.setBlock(pos, newState, 11);
            return true;
        }

        // ---- Wall Banners ----
        if (block instanceof WallBannerBlock) {
            Block newBanner = wallBannerByColor(color);
            if (newBanner == null || newBanner == block) return false;

            BlockState newState = copyCommonProperties(state, newBanner.defaultBlockState());
            // preserve facing
            if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) && newState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                newState = newState.setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
            }
            level.setBlock(pos, newState, 11);
            return true;
        }

        // ---- Wool ----
        if (isAnyWool(block)) {
            Block newWool = woolByColor(color);
            if (newWool == null || newWool == block) return false;
            level.setBlock(pos, copyCommonProperties(state, newWool.defaultBlockState()), 11);
            return true;
        }

        // ---- Carpets ----
        if (isAnyCarpet(block)) {
            Block newCarpet = carpetByColor(color);
            if (newCarpet == null || newCarpet == block) return false;
            level.setBlock(pos, copyCommonProperties(state, newCarpet.defaultBlockState()), 11);
            return true;
        }

        // ---- Terracotta (stained) ----
        if (isAnyTerracotta(block)) {
            Block stained = terracottaByColor(color);
            if (stained == null || stained == block) return false;
            level.setBlock(pos, copyCommonProperties(state, stained.defaultBlockState()), 11);
            return true;
        }

        // ---- Glazed Terracotta ----
        if (isAnyGlazedTerracotta(block)) {
            Block glazed = glazedTerracottaByColor(color);
            if (glazed == null || glazed == block) return false;
            BlockState newState = copyCommonProperties(state, glazed.defaultBlockState());
            // preserve facing
            if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) && newState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                newState = newState.setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
            }
            level.setBlock(pos, newState, 11);
            return true;
        }

        // ---- Stained Glass ----
        if (isAnyStainedGlass(block)) {
            Block glass = stainedGlassByColor(color);
            if (glass == null || glass == block) return false;
            level.setBlock(pos, copyCommonProperties(state, glass.defaultBlockState()), 11);
            return true;
        }

        // ---- Stained Glass Pane ----
        if (isAnyStainedGlassPane(block)) {
            Block pane = stainedGlassPaneByColor(color);
            if (pane == null || pane == block) return false;
            level.setBlock(pos, copyCommonProperties(state, pane.defaultBlockState()), 11);
            return true;
        }

        // ---- Concrete ----
        if (isAnyConcrete(block)) {
            Block concrete = concreteByColor(color);
            if (concrete == null || concrete == block) return false;
            level.setBlock(pos, copyCommonProperties(state, concrete.defaultBlockState()), 11);
            return true;
        }

        // ---- Concrete Powder ----
        if (isAnyConcretePowder(block)) {
            Block powder = concretePowderByColor(color);
            if (powder == null || powder == block) return false;
            level.setBlock(pos, copyCommonProperties(state, powder.defaultBlockState()), 11);
            return true;
        }

        return false;
    }

    // --- Common property copier (Preserves rotation/facing/waterlogged/lit/etc. when present) ---
    private static BlockState copyCommonProperties(BlockState from, BlockState to) {
        for (Property<?> p : from.getProperties()) {
            if (to.getProperties().contains(p)) {
                to = setUnchecked(to, p, from.getValue(p));
            }
        }
        return to;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends Comparable<T>> BlockState setUnchecked(BlockState state, Property p, Comparable v) {
        return state.setValue(p, (T) v);
    }

    // --- Category predicates ---

    private static boolean isAnyWool(Block b) {
        return b == Blocks.WHITE_WOOL || b == Blocks.LIGHT_GRAY_WOOL || b == Blocks.GRAY_WOOL || b == Blocks.BLACK_WOOL ||
                b == Blocks.BROWN_WOOL || b == Blocks.RED_WOOL || b == Blocks.ORANGE_WOOL || b == Blocks.YELLOW_WOOL ||
                b == Blocks.LIME_WOOL || b == Blocks.GREEN_WOOL || b == Blocks.CYAN_WOOL || b == Blocks.LIGHT_BLUE_WOOL ||
                b == Blocks.BLUE_WOOL || b == Blocks.PURPLE_WOOL || b == Blocks.MAGENTA_WOOL || b == Blocks.PINK_WOOL;
    }

    private static boolean isAnyCarpet(Block b) {
        return b == Blocks.WHITE_CARPET || b == Blocks.LIGHT_GRAY_CARPET || b == Blocks.GRAY_CARPET || b == Blocks.BLACK_CARPET ||
                b == Blocks.BROWN_CARPET || b == Blocks.RED_CARPET || b == Blocks.ORANGE_CARPET || b == Blocks.YELLOW_CARPET ||
                b == Blocks.LIME_CARPET || b == Blocks.GREEN_CARPET || b == Blocks.CYAN_CARPET || b == Blocks.LIGHT_BLUE_CARPET ||
                b == Blocks.BLUE_CARPET || b == Blocks.PURPLE_CARPET || b == Blocks.MAGENTA_CARPET || b == Blocks.PINK_CARPET;
    }

    private static boolean isAnyTerracotta(Block b) {
        return b == Blocks.WHITE_TERRACOTTA || b == Blocks.LIGHT_GRAY_TERRACOTTA || b == Blocks.GRAY_TERRACOTTA ||
                b == Blocks.BLACK_TERRACOTTA || b == Blocks.BROWN_TERRACOTTA || b == Blocks.RED_TERRACOTTA ||
                b == Blocks.ORANGE_TERRACOTTA || b == Blocks.YELLOW_TERRACOTTA || b == Blocks.LIME_TERRACOTTA ||
                b == Blocks.GREEN_TERRACOTTA || b == Blocks.CYAN_TERRACOTTA || b == Blocks.LIGHT_BLUE_TERRACOTTA ||
                b == Blocks.BLUE_TERRACOTTA || b == Blocks.PURPLE_TERRACOTTA || b == Blocks.MAGENTA_TERRACOTTA ||
                b == Blocks.PINK_TERRACOTTA;
    }

    private static boolean isAnyShulkerBox(Block b) {
        return b == Blocks.SHULKER_BOX ||
                b == Blocks.WHITE_SHULKER_BOX || b == Blocks.LIGHT_GRAY_SHULKER_BOX || b == Blocks.GRAY_SHULKER_BOX ||
                b == Blocks.BLACK_SHULKER_BOX || b == Blocks.BROWN_SHULKER_BOX || b == Blocks.RED_SHULKER_BOX ||
                b == Blocks.ORANGE_SHULKER_BOX || b == Blocks.YELLOW_SHULKER_BOX || b == Blocks.LIME_SHULKER_BOX ||
                b == Blocks.GREEN_SHULKER_BOX || b == Blocks.CYAN_SHULKER_BOX || b == Blocks.LIGHT_BLUE_SHULKER_BOX ||
                b == Blocks.BLUE_SHULKER_BOX || b == Blocks.PURPLE_SHULKER_BOX || b == Blocks.MAGENTA_SHULKER_BOX ||
                b == Blocks.PINK_SHULKER_BOX;
    }

    private static boolean isAnyGlazedTerracotta(Block b) {
        return b == Blocks.WHITE_GLAZED_TERRACOTTA || b == Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA || b == Blocks.GRAY_GLAZED_TERRACOTTA ||
                b == Blocks.BLACK_GLAZED_TERRACOTTA || b == Blocks.BROWN_GLAZED_TERRACOTTA || b == Blocks.RED_GLAZED_TERRACOTTA ||
                b == Blocks.ORANGE_GLAZED_TERRACOTTA || b == Blocks.YELLOW_GLAZED_TERRACOTTA || b == Blocks.LIME_GLAZED_TERRACOTTA ||
                b == Blocks.GREEN_GLAZED_TERRACOTTA || b == Blocks.CYAN_GLAZED_TERRACOTTA || b == Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA ||
                b == Blocks.BLUE_GLAZED_TERRACOTTA || b == Blocks.PURPLE_GLAZED_TERRACOTTA || b == Blocks.MAGENTA_GLAZED_TERRACOTTA ||
                b == Blocks.PINK_GLAZED_TERRACOTTA;
    }

    private static boolean isAnyStainedGlass(Block b) {
        return b == Blocks.WHITE_STAINED_GLASS || b == Blocks.LIGHT_GRAY_STAINED_GLASS || b == Blocks.GRAY_STAINED_GLASS ||
                b == Blocks.BLACK_STAINED_GLASS || b == Blocks.BROWN_STAINED_GLASS || b == Blocks.RED_STAINED_GLASS ||
                b == Blocks.ORANGE_STAINED_GLASS || b == Blocks.YELLOW_STAINED_GLASS || b == Blocks.LIME_STAINED_GLASS ||
                b == Blocks.GREEN_STAINED_GLASS || b == Blocks.CYAN_STAINED_GLASS || b == Blocks.LIGHT_BLUE_STAINED_GLASS ||
                b == Blocks.BLUE_STAINED_GLASS || b == Blocks.PURPLE_STAINED_GLASS || b == Blocks.MAGENTA_STAINED_GLASS ||
                b == Blocks.PINK_STAINED_GLASS;
    }

    private static boolean isAnyStainedGlassPane(Block b) {
        return b == Blocks.WHITE_STAINED_GLASS_PANE || b == Blocks.LIGHT_GRAY_STAINED_GLASS_PANE || b == Blocks.GRAY_STAINED_GLASS_PANE ||
                b == Blocks.BLACK_STAINED_GLASS_PANE || b == Blocks.BROWN_STAINED_GLASS_PANE || b == Blocks.RED_STAINED_GLASS_PANE ||
                b == Blocks.ORANGE_STAINED_GLASS_PANE || b == Blocks.YELLOW_STAINED_GLASS_PANE || b == Blocks.LIME_STAINED_GLASS_PANE ||
                b == Blocks.GREEN_STAINED_GLASS_PANE || b == Blocks.CYAN_STAINED_GLASS_PANE || b == Blocks.LIGHT_BLUE_STAINED_GLASS_PANE ||
                b == Blocks.BLUE_STAINED_GLASS_PANE || b == Blocks.PURPLE_STAINED_GLASS_PANE || b == Blocks.MAGENTA_STAINED_GLASS_PANE ||
                b == Blocks.PINK_STAINED_GLASS_PANE;
    }

    private static boolean isAnyConcrete(Block b) {
        return b == Blocks.WHITE_CONCRETE || b == Blocks.LIGHT_GRAY_CONCRETE || b == Blocks.GRAY_CONCRETE ||
                b == Blocks.BLACK_CONCRETE || b == Blocks.BROWN_CONCRETE || b == Blocks.RED_CONCRETE ||
                b == Blocks.ORANGE_CONCRETE || b == Blocks.YELLOW_CONCRETE || b == Blocks.LIME_CONCRETE ||
                b == Blocks.GREEN_CONCRETE || b == Blocks.CYAN_CONCRETE || b == Blocks.LIGHT_BLUE_CONCRETE ||
                b == Blocks.BLUE_CONCRETE || b == Blocks.PURPLE_CONCRETE || b == Blocks.MAGENTA_CONCRETE ||
                b == Blocks.PINK_CONCRETE;
    }

    private static boolean isAnyConcretePowder(Block b) {
        return b == Blocks.WHITE_CONCRETE_POWDER || b == Blocks.LIGHT_GRAY_CONCRETE_POWDER || b == Blocks.GRAY_CONCRETE_POWDER ||
                b == Blocks.BLACK_CONCRETE_POWDER || b == Blocks.BROWN_CONCRETE_POWDER || b == Blocks.RED_CONCRETE_POWDER ||
                b == Blocks.ORANGE_CONCRETE_POWDER || b == Blocks.YELLOW_CONCRETE_POWDER || b == Blocks.LIME_CONCRETE_POWDER ||
                b == Blocks.GREEN_CONCRETE_POWDER || b == Blocks.CYAN_CONCRETE_POWDER || b == Blocks.LIGHT_BLUE_CONCRETE_POWDER ||
                b == Blocks.BLUE_CONCRETE_POWDER || b == Blocks.PURPLE_CONCRETE_POWDER || b == Blocks.MAGENTA_CONCRETE_POWDER ||
                b == Blocks.PINK_CONCRETE_POWDER;
    }

    // --- Per-family resolvers ---

    private static Block woolByColor(DyeColor c) {
        return switch (c) {
            case WHITE -> Blocks.WHITE_WOOL;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_WOOL;
            case GRAY -> Blocks.GRAY_WOOL;
            case BLACK -> Blocks.BLACK_WOOL;
            case BROWN -> Blocks.BROWN_WOOL;
            case RED -> Blocks.RED_WOOL;
            case ORANGE -> Blocks.ORANGE_WOOL;
            case YELLOW -> Blocks.YELLOW_WOOL;
            case LIME -> Blocks.LIME_WOOL;
            case GREEN -> Blocks.GREEN_WOOL;
            case CYAN -> Blocks.CYAN_WOOL;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_WOOL;
            case BLUE -> Blocks.BLUE_WOOL;
            case PURPLE -> Blocks.PURPLE_WOOL;
            case MAGENTA -> Blocks.MAGENTA_WOOL;
            case PINK -> Blocks.PINK_WOOL;
        };
    }

    private static Block carpetByColor(DyeColor c) {
        return switch (c) {
            case WHITE -> Blocks.WHITE_CARPET;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_CARPET;
            case GRAY -> Blocks.GRAY_CARPET;
            case BLACK -> Blocks.BLACK_CARPET;
            case BROWN -> Blocks.BROWN_CARPET;
            case RED -> Blocks.RED_CARPET;
            case ORANGE -> Blocks.ORANGE_CARPET;
            case YELLOW -> Blocks.YELLOW_CARPET;
            case LIME -> Blocks.LIME_CARPET;
            case GREEN -> Blocks.GREEN_CARPET;
            case CYAN -> Blocks.CYAN_CARPET;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_CARPET;
            case BLUE -> Blocks.BLUE_CARPET;
            case PURPLE -> Blocks.PURPLE_CARPET;
            case MAGENTA -> Blocks.MAGENTA_CARPET;
            case PINK -> Blocks.PINK_CARPET;
        };
    }

    private static Block terracottaByColor(DyeColor c) {
        return switch (c) {
            case WHITE -> Blocks.WHITE_TERRACOTTA;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_TERRACOTTA;
            case GRAY -> Blocks.GRAY_TERRACOTTA;
            case BLACK -> Blocks.BLACK_TERRACOTTA;
            case BROWN -> Blocks.BROWN_TERRACOTTA;
            case RED -> Blocks.RED_TERRACOTTA;
            case ORANGE -> Blocks.ORANGE_TERRACOTTA;
            case YELLOW -> Blocks.YELLOW_TERRACOTTA;
            case LIME -> Blocks.LIME_TERRACOTTA;
            case GREEN -> Blocks.GREEN_TERRACOTTA;
            case CYAN -> Blocks.CYAN_TERRACOTTA;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_TERRACOTTA;
            case BLUE -> Blocks.BLUE_TERRACOTTA;
            case PURPLE -> Blocks.PURPLE_TERRACOTTA;
            case MAGENTA -> Blocks.MAGENTA_TERRACOTTA;
            case PINK -> Blocks.PINK_TERRACOTTA;
        };
    }

    private static Block shulkerByColor(DyeColor c) {
        return switch (c) {
            case WHITE -> Blocks.WHITE_SHULKER_BOX;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_SHULKER_BOX;
            case GRAY -> Blocks.GRAY_SHULKER_BOX;
            case BLACK -> Blocks.BLACK_SHULKER_BOX;
            case BROWN -> Blocks.BROWN_SHULKER_BOX;
            case RED -> Blocks.RED_SHULKER_BOX;
            case ORANGE -> Blocks.ORANGE_SHULKER_BOX;
            case YELLOW -> Blocks.YELLOW_SHULKER_BOX;
            case LIME -> Blocks.LIME_SHULKER_BOX;
            case GREEN -> Blocks.GREEN_SHULKER_BOX;
            case CYAN -> Blocks.CYAN_SHULKER_BOX;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_SHULKER_BOX;
            case BLUE -> Blocks.BLUE_SHULKER_BOX;
            case PURPLE -> Blocks.PURPLE_SHULKER_BOX;
            case MAGENTA -> Blocks.MAGENTA_SHULKER_BOX;
            case PINK -> Blocks.PINK_SHULKER_BOX;
        };
    }

    private static Block glazedTerracottaByColor(DyeColor c) {
        return switch (c) {
            case WHITE -> Blocks.WHITE_GLAZED_TERRACOTTA;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA;
            case GRAY -> Blocks.GRAY_GLAZED_TERRACOTTA;
            case BLACK -> Blocks.BLACK_GLAZED_TERRACOTTA;
            case BROWN -> Blocks.BROWN_GLAZED_TERRACOTTA;
            case RED -> Blocks.RED_GLAZED_TERRACOTTA;
            case ORANGE -> Blocks.ORANGE_GLAZED_TERRACOTTA;
            case YELLOW -> Blocks.YELLOW_GLAZED_TERRACOTTA;
            case LIME -> Blocks.LIME_GLAZED_TERRACOTTA;
            case GREEN -> Blocks.GREEN_GLAZED_TERRACOTTA;
            case CYAN -> Blocks.CYAN_GLAZED_TERRACOTTA;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA;
            case BLUE -> Blocks.BLUE_GLAZED_TERRACOTTA;
            case PURPLE -> Blocks.PURPLE_GLAZED_TERRACOTTA;
            case MAGENTA -> Blocks.MAGENTA_GLAZED_TERRACOTTA;
            case PINK -> Blocks.PINK_GLAZED_TERRACOTTA;
        };
    }

    private static Block stainedGlassByColor(DyeColor c) {
        return switch (c) {
            case WHITE -> Blocks.WHITE_STAINED_GLASS;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_STAINED_GLASS;
            case GRAY -> Blocks.GRAY_STAINED_GLASS;
            case BLACK -> Blocks.BLACK_STAINED_GLASS;
            case BROWN -> Blocks.BROWN_STAINED_GLASS;
            case RED -> Blocks.RED_STAINED_GLASS;
            case ORANGE -> Blocks.ORANGE_STAINED_GLASS;
            case YELLOW -> Blocks.YELLOW_STAINED_GLASS;
            case LIME -> Blocks.LIME_STAINED_GLASS;
            case GREEN -> Blocks.GREEN_STAINED_GLASS;
            case CYAN -> Blocks.CYAN_STAINED_GLASS;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_STAINED_GLASS;
            case BLUE -> Blocks.BLUE_STAINED_GLASS;
            case PURPLE -> Blocks.PURPLE_STAINED_GLASS;
            case MAGENTA -> Blocks.MAGENTA_STAINED_GLASS;
            case PINK -> Blocks.PINK_STAINED_GLASS;
        };
    }

    private static Block stainedGlassPaneByColor(DyeColor c) {
        return switch (c) {
            case WHITE -> Blocks.WHITE_STAINED_GLASS_PANE;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_STAINED_GLASS_PANE;
            case GRAY -> Blocks.GRAY_STAINED_GLASS_PANE;
            case BLACK -> Blocks.BLACK_STAINED_GLASS_PANE;
            case BROWN -> Blocks.BROWN_STAINED_GLASS_PANE;
            case RED -> Blocks.RED_STAINED_GLASS_PANE;
            case ORANGE -> Blocks.ORANGE_STAINED_GLASS_PANE;
            case YELLOW -> Blocks.YELLOW_STAINED_GLASS_PANE;
            case LIME -> Blocks.LIME_STAINED_GLASS_PANE;
            case GREEN -> Blocks.GREEN_STAINED_GLASS_PANE;
            case CYAN -> Blocks.CYAN_STAINED_GLASS_PANE;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_STAINED_GLASS_PANE;
            case BLUE -> Blocks.BLUE_STAINED_GLASS_PANE;
            case PURPLE -> Blocks.PURPLE_STAINED_GLASS_PANE;
            case MAGENTA -> Blocks.MAGENTA_STAINED_GLASS_PANE;
            case PINK -> Blocks.PINK_STAINED_GLASS_PANE;
        };
    }

    private static Block concreteByColor(DyeColor c) {
        return switch (c) {
            case WHITE -> Blocks.WHITE_CONCRETE;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_CONCRETE;
            case GRAY -> Blocks.GRAY_CONCRETE;
            case BLACK -> Blocks.BLACK_CONCRETE;
            case BROWN -> Blocks.BROWN_CONCRETE;
            case RED -> Blocks.RED_CONCRETE;
            case ORANGE -> Blocks.ORANGE_CONCRETE;
            case YELLOW -> Blocks.YELLOW_CONCRETE;
            case LIME -> Blocks.LIME_CONCRETE;
            case GREEN -> Blocks.GREEN_CONCRETE;
            case CYAN -> Blocks.CYAN_CONCRETE;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_CONCRETE;
            case BLUE -> Blocks.BLUE_CONCRETE;
            case PURPLE -> Blocks.PURPLE_CONCRETE;
            case MAGENTA -> Blocks.MAGENTA_CONCRETE;
            case PINK -> Blocks.PINK_CONCRETE;
        };
    }

    private static Block concretePowderByColor(DyeColor c) {
        return switch (c) {
            case WHITE -> Blocks.WHITE_CONCRETE_POWDER;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_CONCRETE_POWDER;
            case GRAY -> Blocks.GRAY_CONCRETE_POWDER;
            case BLACK -> Blocks.BLACK_CONCRETE_POWDER;
            case BROWN -> Blocks.BROWN_CONCRETE_POWDER;
            case RED -> Blocks.RED_CONCRETE_POWDER;
            case ORANGE -> Blocks.ORANGE_CONCRETE_POWDER;
            case YELLOW -> Blocks.YELLOW_CONCRETE_POWDER;
            case LIME -> Blocks.LIME_CONCRETE_POWDER;
            case GREEN -> Blocks.GREEN_CONCRETE_POWDER;
            case CYAN -> Blocks.CYAN_CONCRETE_POWDER;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_CONCRETE_POWDER;
            case BLUE -> Blocks.BLUE_CONCRETE_POWDER;
            case PURPLE -> Blocks.PURPLE_CONCRETE_POWDER;
            case MAGENTA -> Blocks.MAGENTA_CONCRETE_POWDER;
            case PINK -> Blocks.PINK_CONCRETE_POWDER;
        };
    }

    private static Block bedByColor(DyeColor c) {
        return switch (c) {
            case WHITE -> Blocks.WHITE_BED;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_BED;
            case GRAY -> Blocks.GRAY_BED;
            case BLACK -> Blocks.BLACK_BED;
            case BROWN -> Blocks.BROWN_BED;
            case RED -> Blocks.RED_BED;
            case ORANGE -> Blocks.ORANGE_BED;
            case YELLOW -> Blocks.YELLOW_BED;
            case LIME -> Blocks.LIME_BED;
            case GREEN -> Blocks.GREEN_BED;
            case CYAN -> Blocks.CYAN_BED;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_BED;
            case BLUE -> Blocks.BLUE_BED;
            case PURPLE -> Blocks.PURPLE_BED;
            case MAGENTA -> Blocks.MAGENTA_BED;
            case PINK -> Blocks.PINK_BED;
        };
    }

    private static Block candleByColor(DyeColor c) {
        return switch (c) {
            case WHITE -> Blocks.WHITE_CANDLE;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_CANDLE;
            case GRAY -> Blocks.GRAY_CANDLE;
            case BLACK -> Blocks.BLACK_CANDLE;
            case BROWN -> Blocks.BROWN_CANDLE;
            case RED -> Blocks.RED_CANDLE;
            case ORANGE -> Blocks.ORANGE_CANDLE;
            case YELLOW -> Blocks.YELLOW_CANDLE;
            case LIME -> Blocks.LIME_CANDLE;
            case GREEN -> Blocks.GREEN_CANDLE;
            case CYAN -> Blocks.CYAN_CANDLE;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_CANDLE;
            case BLUE -> Blocks.BLUE_CANDLE;
            case PURPLE -> Blocks.PURPLE_CANDLE;
            case MAGENTA -> Blocks.MAGENTA_CANDLE;
            case PINK -> Blocks.PINK_CANDLE;
        };
    }

    private static Block candleCakeByColor(DyeColor c) {
        return switch (c) {
            case WHITE -> Blocks.WHITE_CANDLE_CAKE;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_CANDLE_CAKE;
            case GRAY -> Blocks.GRAY_CANDLE_CAKE;
            case BLACK -> Blocks.BLACK_CANDLE_CAKE;
            case BROWN -> Blocks.BROWN_CANDLE_CAKE;
            case RED -> Blocks.RED_CANDLE_CAKE;
            case ORANGE -> Blocks.ORANGE_CANDLE_CAKE;
            case YELLOW -> Blocks.YELLOW_CANDLE_CAKE;
            case LIME -> Blocks.LIME_CANDLE_CAKE;
            case GREEN -> Blocks.GREEN_CANDLE_CAKE;
            case CYAN -> Blocks.CYAN_CANDLE_CAKE;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_CANDLE_CAKE;
            case BLUE -> Blocks.BLUE_CANDLE_CAKE;
            case PURPLE -> Blocks.PURPLE_CANDLE_CAKE;
            case MAGENTA -> Blocks.MAGENTA_CANDLE_CAKE;
            case PINK -> Blocks.PINK_CANDLE_CAKE;
        };
    }

    private static Block standingBannerByColor(DyeColor c) {
        return switch (c) {
            case WHITE -> Blocks.WHITE_BANNER;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_BANNER;
            case GRAY -> Blocks.GRAY_BANNER;
            case BLACK -> Blocks.BLACK_BANNER;
            case BROWN -> Blocks.BROWN_BANNER;
            case RED -> Blocks.RED_BANNER;
            case ORANGE -> Blocks.ORANGE_BANNER;
            case YELLOW -> Blocks.YELLOW_BANNER;
            case LIME -> Blocks.LIME_BANNER;
            case GREEN -> Blocks.GREEN_BANNER;
            case CYAN -> Blocks.CYAN_BANNER;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_BANNER;
            case BLUE -> Blocks.BLUE_BANNER;
            case PURPLE -> Blocks.PURPLE_BANNER;
            case MAGENTA -> Blocks.MAGENTA_BANNER;
            case PINK -> Blocks.PINK_BANNER;
        };
    }

    private static Block wallBannerByColor(DyeColor c) {
        return switch (c) {
            case WHITE -> Blocks.WHITE_WALL_BANNER;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_WALL_BANNER;
            case GRAY -> Blocks.GRAY_WALL_BANNER;
            case BLACK -> Blocks.BLACK_WALL_BANNER;
            case BROWN -> Blocks.BROWN_WALL_BANNER;
            case RED -> Blocks.RED_WALL_BANNER;
            case ORANGE -> Blocks.ORANGE_WALL_BANNER;
            case YELLOW -> Blocks.YELLOW_WALL_BANNER;
            case LIME -> Blocks.LIME_WALL_BANNER;
            case GREEN -> Blocks.GREEN_WALL_BANNER;
            case CYAN -> Blocks.CYAN_WALL_BANNER;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_WALL_BANNER;
            case BLUE -> Blocks.BLUE_WALL_BANNER;
            case PURPLE -> Blocks.PURPLE_WALL_BANNER;
            case MAGENTA -> Blocks.MAGENTA_WALL_BANNER;
            case PINK -> Blocks.PINK_WALL_BANNER;
        };
    }
}
package com.styenvy.egtools;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Construction Hammer - A tool that cycles through block variants when left-clicking.
 * Left-click: cycle forward through variants
 * Shift+Left-click: cycle backward through variants
 * Special handling for slabs and doors:
 * - When cycling TO a slab: always creates bottom slab
 * - When cycling FROM a top slab: moves it down if space below is free
 * - When cycling TO a door: creates both halves properly
 */
public class ConstructionHammerItem extends Item {

    public ConstructionHammerItem(Properties properties) {
        super(properties);
    }

    /**
     * Override to prevent breaking blocks when left-clicking.
     * This is called when the player left-clicks a block.
     */
    @Override
    public boolean canAttackBlock(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player) {
        // Return false to prevent breaking the block
        // Instead, we'll handle the cycling logic here
        if (!level.isClientSide) {
            cycleBlock(level, pos, player, player.isShiftKeyDown());
        }
        return false; // Prevent block breaking
    }

    /**
     * Main cycling logic moved to a separate method.
     */
    private void cycleBlock(Level level, BlockPos pos, Player player, boolean cycleBackward) {
        BlockState currentState = level.getBlockState(pos);
        Block currentBlock = currentState.getBlock();

        // Special handling for top slabs when cycling
        if (currentBlock instanceof SlabBlock && currentState.getValue(SlabBlock.TYPE) == SlabType.TOP) {
            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);

            // If cycling from a top slab and space below is free, move it down
            if (belowState.isAir() && !cycleBackward) {
                // Move the slab down to bottom position
                BlockState bottomSlab = currentState.setValue(SlabBlock.TYPE, SlabType.BOTTOM);
                level.setBlock(belowPos, bottomSlab, 3);
                level.setBlock(pos, level.getBlockState(pos.above()).isAir() ?
                        net.minecraft.world.level.block.Blocks.AIR.defaultBlockState() : currentState, 3);
                return;
            }
            // If block below exists, don't cycle from top slab
            if (!belowState.isAir()) {
                return;
            }
        }

        // Get the next block in the cycle
        BlockVariantCycle cycle = BlockVariantRegistry.getCycle(currentBlock);
        if (cycle == null) {
            return;
        }

        Block nextBlock = cycleBackward ? cycle.getPrevious(currentBlock) : cycle.getNext(currentBlock);
        if (nextBlock == null || nextBlock == currentBlock) {
            return;
        }

        // Create the new block state
        BlockState newState = nextBlock.defaultBlockState();

        // Special handling for doors - need to place both halves
        if (newState.getBlock() instanceof DoorBlock) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);

            // Only place door if there's space above
            if (aboveState.isAir() || aboveState.canBeReplaced()) {
                // Create bottom half
                BlockState bottomHalf = newState.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER);
                bottomHalf = preserveCompatibleProperties(currentState, bottomHalf);

                // Create top half
                BlockState topHalf = newState.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
                topHalf = preserveCompatibleProperties(currentState, topHalf);

                // Place both halves
                level.setBlock(pos, bottomHalf, 3);
                level.setBlock(abovePos, topHalf, 3);
                return;
            } else {
                // Can't place door, skip to next block in cycle
                return;
            }
        }

        // Special handling for slabs - always create as bottom slab
        if (newState.hasProperty(SlabBlock.TYPE)) {
            newState = newState.setValue(SlabBlock.TYPE, SlabType.BOTTOM);
        }

        // Preserve compatible properties from the old state
        newState = preserveCompatibleProperties(currentState, newState);

        // Set the new block
        level.setBlock(pos, newState, 3);
    }

    /**
     * Preserves compatible BlockState properties when cycling between blocks.
     * This maintains properties like facing, waterlogged, powered, etc. when both blocks support them.
     */
    private BlockState preserveCompatibleProperties(BlockState oldState, BlockState newState) {
        // Iterate through all properties of the old state
        for (Property<?> property : oldState.getProperties()) {
            // Check if the new state has the same property
            if (newState.hasProperty(property)) {
                try {
                    // Special handling for slab type - always use bottom
                    if (property == SlabBlock.TYPE) {
                        newState = newState.setValue(SlabBlock.TYPE, SlabType.BOTTOM);
                    }
                    // Skip door half property as we handle it separately
                    else if (property == DoorBlock.HALF) {
                        // Don't copy this property
                    } else {
                        // Copy the property value
                        newState = copyProperty(oldState, newState, property);
                    }
                } catch (IllegalArgumentException e) {
                    // Property exists but value is not valid for new block, skip it
                }
            }
        }

        return newState;
    }

    /**
     * Helper method to copy a property value from one state to another.
     */
    private <T extends Comparable<T>> BlockState copyProperty(BlockState source, BlockState target, Property<T> property) {
        return target.setValue(property, source.getValue(property));
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        // Add enchantment glint for visual distinction
        return true;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.egtools.construction_hammer.tooltip1"));
        tooltip.add(Component.translatable("item.egtools.construction_hammer.tooltip2"));
        tooltip.add(Component.translatable("item.egtools.construction_hammer.tooltip3"));
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false; // No enchantments needed
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return 1; // Only one hammer per stack
    }

    @Override
    public boolean isDamageable(@NotNull ItemStack stack) {
        return false; // Unlimited durability
    }

    /**
     * Override to ensure the hammer has a proper mining speed but doesn't actually break blocks.
     */
    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        return 0.0F; // No mining speed since we don't want to break blocks
    }
}
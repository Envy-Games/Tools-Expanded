package com.styenvy.egtools;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.jetbrains.annotations.NotNull;

public class PaxelItem extends Item implements IItemExtension {
    private final Tier tier;

    public PaxelItem(Tier tier, Item.Properties props) {
        super(props.durability(tier.getUses()));
        this.tier = tier;
    }

    /** Multitool abilities: pickaxe + axe + shovel + hoe + sword + shears. */
    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ItemAbility ability) {
        return  ability == ItemAbilities.PICKAXE_DIG
                || ability == ItemAbilities.AXE_DIG
                || ability == ItemAbilities.AXE_STRIP
                || ability == ItemAbilities.AXE_SCRAPE
                || ability == ItemAbilities.AXE_WAX_OFF
                || ability == ItemAbilities.SHOVEL_DIG
                || ability == ItemAbilities.SHOVEL_FLATTEN
                || ability == ItemAbilities.HOE_TILL
                || ability == ItemAbilities.SWORD_DIG
                // Shears family (1.21.1 docs)
                || ability == ItemAbilities.SHEARS_DIG
                || ability == ItemAbilities.SHEARS_CARVE
                || ability == ItemAbilities.SHEARS_DISARM
                || ability == ItemAbilities.SHEARS_HARVEST;
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)
                || state.is(BlockTags.MINEABLE_WITH_AXE)
                || state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            return this.tier.getSpeed();
        }
        return 1.0f;
    }

    @Override
    public boolean isCorrectToolForDrops(@NotNull ItemStack stack, BlockState state) {
        if (state.is(this.tier.getIncorrectBlocksForDrops())) return false;
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE)
                || state.is(BlockTags.MINEABLE_WITH_AXE)
                || state.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }
}

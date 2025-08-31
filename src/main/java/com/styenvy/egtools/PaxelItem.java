package com.styenvy.egtools;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaxelItem extends Item implements IItemExtension {
    private static final int DURABILITY_MULT = 6;       // ~6x tier base uses
    private static final float SPEED_MULT     = 1.10F;  // ~10% faster than tier base
    private static final float BASE_SWORD_DMG = 4.0F;   // added to tier bonus
    private static final float ATTACK_SPEED   = -2.6F;  // between sword (-2.4) and axe (-3.1)

    private final Tier tier;

    public PaxelItem(Tier tier, Item.Properties props) {
        super(props
                .durability(Math.max(1, tier.getUses() * DURABILITY_MULT))
                .attributes(createAttributes(tier)));
        this.tier = tier;
    }

    /** Multitool abilities: pickaxe + axe + shovel + hoe + sword (+sweep) + shears family. */
    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ItemAbility ability) {
        if (stack.isDamaged() && stack.getDamageValue() >= stack.getMaxDamage()) return false;
        return  ability == ItemAbilities.PICKAXE_DIG
                || ability == ItemAbilities.AXE_DIG
                || ability == ItemAbilities.AXE_STRIP
                || ability == ItemAbilities.AXE_SCRAPE
                || ability == ItemAbilities.AXE_WAX_OFF
                || ability == ItemAbilities.SHOVEL_DIG
                || ability == ItemAbilities.SHOVEL_FLATTEN
                || ability == ItemAbilities.HOE_TILL
                || ability == ItemAbilities.SWORD_DIG
                || ability == ItemAbilities.SWORD_SWEEP
                || ability == ItemAbilities.SHEARS_DIG
                || ability == ItemAbilities.SHEARS_CARVE
                || ability == ItemAbilities.SHEARS_DISARM
                || ability == ItemAbilities.SHEARS_HARVEST;
    }

    /** Right-click block actions with appropriate sounds/particles. */
    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext ctx) {
        final Level level = ctx.getLevel();
        final BlockPos pos = ctx.getClickedPos();
        final BlockState state = level.getBlockState(pos);
        final ItemStack stack = ctx.getItemInHand();
        final Player player = ctx.getPlayer();

        if (tryModify(state, ctx, ItemAbilities.AXE_STRIP, SoundEvents.AXE_STRIP, level, pos, stack, player, 0)) return InteractionResult.sidedSuccess(level.isClientSide);
        if (tryModify(state, ctx, ItemAbilities.AXE_SCRAPE, SoundEvents.AXE_SCRAPE, level, pos, stack, player, 3005)) return InteractionResult.sidedSuccess(level.isClientSide);
        if (tryModify(state, ctx, ItemAbilities.AXE_WAX_OFF, SoundEvents.AXE_WAX_OFF, level, pos, stack, player, 3004)) return InteractionResult.sidedSuccess(level.isClientSide);

        if (tryModify(state, ctx, ItemAbilities.SHOVEL_FLATTEN, SoundEvents.SHOVEL_FLATTEN, level, pos, stack, player, 0)) return InteractionResult.sidedSuccess(level.isClientSide);

        if (tryModify(state, ctx, ItemAbilities.HOE_TILL, SoundEvents.HOE_TILL, level, pos, stack, player, 0)) return InteractionResult.sidedSuccess(level.isClientSide);

        // Shears-style block interactions
        if (tryModify(state, ctx, ItemAbilities.SHEARS_CARVE, SoundEvents.PUMPKIN_CARVE, level, pos, stack, player, 0)) return InteractionResult.sidedSuccess(level.isClientSide);
        if (tryModify(state, ctx, ItemAbilities.SHEARS_DISARM, null, level, pos, stack, player, 0)) return InteractionResult.sidedSuccess(level.isClientSide); // silent disarm
        if (tryModify(state, ctx, ItemAbilities.SHEARS_HARVEST, SoundEvents.BEEHIVE_SHEAR, level, pos, stack, player, 0)) return InteractionResult.sidedSuccess(level.isClientSide);

        return InteractionResult.PASS;
    }

    /** Faster mining across all relevant tags; universal correct-tool for drops on diggables. */
    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)
                || state.is(BlockTags.MINEABLE_WITH_AXE)
                || state.is(BlockTags.MINEABLE_WITH_SHOVEL)
                || state.is(BlockTags.MINEABLE_WITH_HOE)
                || state.is(BlockTags.LEAVES)
                || state.is(BlockTags.WOOL)
                || state.is(BlockTags.SWORD_EFFICIENT)) {
            return this.tier.getSpeed() * SPEED_MULT;
        }
        return 1.0F;
    }

    @Override
    public boolean isCorrectToolForDrops(@NotNull ItemStack stack, BlockState state) {
        if (state.is(this.tier.getIncorrectBlocksForDrops())) return false;
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE)
                || state.is(BlockTags.MINEABLE_WITH_AXE)
                || state.is(BlockTags.MINEABLE_WITH_SHOVEL)
                || state.is(BlockTags.MINEABLE_WITH_HOE);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack,
                                                           @NotNull Player player,
                                                           @NotNull net.minecraft.world.entity.LivingEntity target,
                                                           @NotNull InteractionHand hand) {
        // Delegate to vanilla shears behavior (uses current hooks).
        return net.minecraft.world.item.Items.SHEARS.interactLivingEntity(stack, player, target, hand);
    }

    @Override
    public int getEnchantmentValue(@NotNull ItemStack stack) {
        return this.tier.getEnchantmentValue();
    }


    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repairCandidate) {
        return this.tier.getRepairIngredient().test(repairCandidate);
    }

    // --- Helpers ---

    private static ItemAttributeModifiers createAttributes(Tier tier) {
        double damage = BASE_SWORD_DMG + tier.getAttackDamageBonus();
        var modDamage = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                BASE_ATTACK_DAMAGE_ID, damage, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE);
        var modSpeed = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                BASE_ATTACK_SPEED_ID, ATTACK_SPEED, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE);

        return ItemAttributeModifiers.builder()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, modDamage, EquipmentSlotGroup.MAINHAND)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED,  modSpeed,  EquipmentSlotGroup.MAINHAND)
                .build();
    }

    /** Apply a tool-modified state with optional sound/levelEvent and damage the tool. */
    private static boolean tryModify(BlockState state,
                                     UseOnContext ctx,
                                     ItemAbility ability,
                                     @Nullable net.minecraft.sounds.SoundEvent sound,
                                     Level level, BlockPos pos,
                                     ItemStack stack, @Nullable Player player,
                                     int levelEvent) {
        BlockState modified = state.getToolModifiedState(ctx, ability, false);
        if (modified == null) return false;

        if (sound != null) level.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (levelEvent != 0) level.levelEvent(player, levelEvent, pos, 0);
        level.setBlock(pos, modified, 11);

        if (player != null) {
            EquipmentSlot slot = ctx.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            stack.hurtAndBreak(1, player, slot);
        }
        return true;
    }
}

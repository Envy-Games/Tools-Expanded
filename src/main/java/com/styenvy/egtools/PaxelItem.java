package com.styenvy.egtools;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PaxelItem extends Item implements IItemExtension {
    private static final int DURABILITY_MULT = 6;
    private static final float SPEED_MULT = 1.10F;
    private static final float BASE_SWORD_DMG = 4.0F;
    private static final float ATTACK_SPEED = -2.6F;
    private static final int ATTACK_DAMAGE_DURABILITY_COST = 2;
    private static final int TOOL_ACTION_DURABILITY_COST = 1;

    private final Tier tier;

    public PaxelItem(Tier tier, Item.Properties props) {
        super(props
                .durability(Math.max(1, tier.getUses() * DURABILITY_MULT))
                .component(DataComponents.TOOL, createToolProperties(tier))
                .attributes(createAttributes(tier)));
        this.tier = tier;
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ItemAbility ability) {
        return isUsable(stack)
                && (ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(ability)
                || ItemAbilities.DEFAULT_AXE_ACTIONS.contains(ability)
                || ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(ability)
                || ItemAbilities.DEFAULT_HOE_ACTIONS.contains(ability)
                || ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(ability)
                || ItemAbilities.DEFAULT_SHEARS_ACTIONS.contains(ability));
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext ctx) {
        if (playerHasShieldUseIntent(ctx)) {
            return InteractionResult.PASS;
        }

        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = ctx.getItemInHand();
        Player player = ctx.getPlayer();

        if (tryModify(state, ctx, ItemAbilities.AXE_STRIP, SoundEvents.AXE_STRIP, level, pos, stack, player, 0)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (tryModify(state, ctx, ItemAbilities.AXE_SCRAPE, SoundEvents.AXE_SCRAPE, level, pos, stack, player, 3005)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (tryModify(state, ctx, ItemAbilities.AXE_WAX_OFF, SoundEvents.AXE_WAX_OFF, level, pos, stack, player, 3004)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (canUseShovelAction(ctx)) {
            if (canFlatten(ctx) && tryModify(state, ctx, ItemAbilities.SHOVEL_FLATTEN, SoundEvents.SHOVEL_FLATTEN, level, pos, stack, player, 0)) {
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            if (tryModify(state, ctx, ItemAbilities.SHOVEL_DOUSE, null, level, pos, stack, player, 1009)) {
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        if (tryModify(state, ctx, ItemAbilities.HOE_TILL, SoundEvents.HOE_TILL, level, pos, stack, player, 0)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (tryModify(state, ctx, ItemAbilities.SHEARS_TRIM, null, level, pos, stack, player, 0)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack,
                                                           @NotNull Player player,
                                                           @NotNull LivingEntity target,
                                                           @NotNull InteractionHand hand) {
        return Items.SHEARS.interactLivingEntity(stack, player, target, hand);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        return true;
    }

    @Override
    public void postHurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        stack.hurtAndBreak(ATTACK_DAMAGE_DURABILITY_COST, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public int getEnchantmentValue(@NotNull ItemStack stack) {
        return this.tier.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repairCandidate) {
        return this.tier.getRepairIngredient().test(repairCandidate) || super.isValidRepairItem(toRepair, repairCandidate);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.egtools.paxel.tooltip1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.egtools.paxel.tooltip2").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.egtools.paxel.tooltip3").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.egtools.paxel.tooltip4").withStyle(ChatFormatting.DARK_GRAY));
    }

    private static boolean isUsable(ItemStack stack) {
        return !stack.isDamageableItem() || stack.getDamageValue() < stack.getMaxDamage();
    }

    private static boolean playerHasShieldUseIntent(UseOnContext ctx) {
        Player player = ctx.getPlayer();
        return player != null
                && ctx.getHand() == InteractionHand.MAIN_HAND
                && player.getOffhandItem().is(Items.SHIELD)
                && !player.isSecondaryUseActive();
    }

    private static boolean canUseShovelAction(UseOnContext ctx) {
        return ctx.getClickedFace() != Direction.DOWN;
    }

    private static boolean canFlatten(UseOnContext ctx) {
        return ctx.getLevel().getBlockState(ctx.getClickedPos().above()).isAir();
    }

    private static Tool createToolProperties(Tier tier) {
        float speed = tier.getSpeed() * SPEED_MULT;
        return new Tool(List.of(
                Tool.Rule.deniesDrops(tier.getIncorrectBlocksForDrops()),
                Tool.Rule.minesAndDrops(List.of(Blocks.COBWEB), 15.0F),
                Tool.Rule.overrideSpeed(BlockTags.LEAVES, 15.0F),
                Tool.Rule.overrideSpeed(BlockTags.WOOL, 5.0F),
                Tool.Rule.overrideSpeed(List.of(Blocks.VINE, Blocks.GLOW_LICHEN), 2.0F),
                Tool.Rule.overrideSpeed(BlockTags.SWORD_EFFICIENT, speed),
                Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, speed),
                Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_AXE, speed),
                Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_SHOVEL, speed),
                Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_HOE, speed)
        ), 1.0F, 1);
    }

    private static ItemAttributeModifiers createAttributes(Tier tier) {
        double damage = BASE_SWORD_DMG + tier.getAttackDamageBonus();
        AttributeModifier modDamage = new AttributeModifier(
                BASE_ATTACK_DAMAGE_ID, damage, AttributeModifier.Operation.ADD_VALUE);
        AttributeModifier modSpeed = new AttributeModifier(
                BASE_ATTACK_SPEED_ID, ATTACK_SPEED, AttributeModifier.Operation.ADD_VALUE);

        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, modDamage, EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, modSpeed, EquipmentSlotGroup.MAINHAND)
                .build();
    }

    private static boolean tryModify(BlockState state,
                                     UseOnContext ctx,
                                     ItemAbility ability,
                                     @Nullable SoundEvent sound,
                                     Level level,
                                     BlockPos pos,
                                     ItemStack stack,
                                     @Nullable Player player,
                                     int levelEvent) {
        BlockState modified = state.getToolModifiedState(ctx, ability, false);
        if (modified == null) {
            return false;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
        }
        if (sound != null) {
            level.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        if (levelEvent != 0) {
            if (ability == ItemAbilities.SHOVEL_DOUSE) {
                if (!level.isClientSide) {
                    level.levelEvent(null, levelEvent, pos, 0);
                }
            } else {
                level.levelEvent(player, levelEvent, pos, 0);
            }
        }

        if (!level.isClientSide) {
            level.setBlock(pos, modified, 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, modified));
            if (player != null) {
                stack.hurtAndBreak(TOOL_ACTION_DURABILITY_COST, player, LivingEntity.getSlotForHand(ctx.getHand()));
            }
        }
        return true;
    }
}

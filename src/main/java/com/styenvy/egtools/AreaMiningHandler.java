package com.styenvy.egtools;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.concurrent.atomic.AtomicInteger;

@EventBusSubscriber(modid = EgTools.MODID)
public final class AreaMiningHandler {
    private static final ThreadLocal<Boolean> MINING_EXTRA_BLOCKS = ThreadLocal.withInitial(() -> false);

    private AreaMiningHandler() {}

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled() || MINING_EXTRA_BLOCKS.get()) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        if (!stack.canPerformAction(ItemAbilities.PICKAXE_DIG)) {
            return;
        }

        int radius = getMiningRadius(stack);
        if (radius <= 0) {
            return;
        }

        BlockPos origin = event.getPos();
        if (!canMineExtraBlock(level, player, origin, event.getState(), stack)) {
            return;
        }

        MINING_EXTRA_BLOCKS.set(true);
        try {
            mineNearbyBlocks(level, player, stack, origin, radius);
        } finally {
            MINING_EXTRA_BLOCKS.set(false);
        }
    }

    private static int getMiningRadius(ItemStack stack) {
        AtomicInteger radius = new AtomicInteger();
        EnchantmentHelper.runIterationOnItem(stack, (enchantment, level) -> {
            MiningArea area = enchantment.value().effects().get(EgToolsEnchantmentEffectComponents.MINING_AREA.get());
            if (area != null) {
                radius.set(Math.max(radius.get(), area.radius()));
            }
        });
        return radius.get();
    }

    private static void mineNearbyBlocks(ServerLevel level, ServerPlayer player, ItemStack stack, BlockPos origin, int radius) {
        for (BlockPos target : BlockPos.betweenClosed(
                origin.offset(-radius, -radius, -radius),
                origin.offset(radius, radius, radius))) {
            if (target.equals(origin) || player.getMainHandItem().isEmpty()) {
                continue;
            }

            BlockPos immutableTarget = target.immutable();
            BlockState targetState = level.getBlockState(immutableTarget);
            if (canMineExtraBlock(level, player, immutableTarget, targetState, stack)) {
                player.gameMode.destroyBlock(immutableTarget);
            }
        }
    }

    private static boolean canMineExtraBlock(ServerLevel level, ServerPlayer player, BlockPos pos, BlockState state, ItemStack stack) {
        return !state.isAir()
                && level.mayInteract(player, pos)
                && player.canInteractWithBlock(pos, 1.0)
                && state.getDestroySpeed(level, pos) >= 0.0F
                && stack.isCorrectToolForDrops(state);
    }
}

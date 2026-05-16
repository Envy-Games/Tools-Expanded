package com.styenvy.egtools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BedrockWeakeningHandler {
    private static final int SPLASH_RADIUS = 3;
    private static final float WEAKENED_BEDROCK_HARDNESS = 50.0F;
    private static final BlockState MINING_SURROGATE = Blocks.OBSIDIAN.defaultBlockState();
    private static final Map<ResourceKey<Level>, Map<BlockPos, Long>> WEAKENED_BLOCKS = new HashMap<>();
    private static final Map<UUID, MiningProgress> ACTIVE_MINING = new HashMap<>();

    private BedrockWeakeningHandler() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(BedrockWeakeningHandler::registerBrewingRecipes);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, BedrockWeakeningHandler::onPotionImpact);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, true, BedrockWeakeningHandler::onLeftClickBlock);
        NeoForge.EVENT_BUS.addListener(BedrockWeakeningHandler::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(BedrockWeakeningHandler::onLevelTick);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, BedrockWeakeningHandler::onHarvestCheck);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, BedrockWeakeningHandler::onBlockDrops);
        NeoForge.EVENT_BUS.addListener(BedrockWeakeningHandler::onPlayerLoggedOut);
        NeoForge.EVENT_BUS.addListener(BedrockWeakeningHandler::onPlayerChangedDimension);
        NeoForge.EVENT_BUS.addListener(BedrockWeakeningHandler::onPlayerClone);
        NeoForge.EVENT_BUS.addListener(BedrockWeakeningHandler::onServerStopped);
    }

    private static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        event.getBuilder().addMix(Potions.AWKWARD, Items.CRYING_OBSIDIAN, EgToolsPotions.WEAKENING);
    }

    private static void onPotionImpact(ProjectileImpactEvent event) {
        if (event.isCanceled()
                || !(event.getProjectile() instanceof ThrownPotion potion)
                || !(potion.level() instanceof ServerLevel level)) {
            return;
        }

        PotionContents contents = potion.getItem().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        int duration = getWeakeningDuration(contents);
        if (duration <= 0) {
            return;
        }

        weakenBlocks(level, impactCenter(event.getRayTraceResult()), duration);
    }

    private static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (event.isCanceled()) {
            if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.ABORT
                    || event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.STOP) {
                clearMining(player);
            }
            return;
        }

        if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.START) {
            if (!event.getUseItem().isFalse() && canMineWeakenedBedrock(player, level, event.getPos())) {
                startMining(player, level, event.getPos());
                event.setCanceled(true);
            } else {
                clearMining(player);
            }
        } else if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.ABORT
                || event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.STOP) {
            if (clearMining(player)) {
                event.setCanceled(true);
            }
        }
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            tickMining(player);
        }
    }

    private static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        Map<BlockPos, Long> weakened = WEAKENED_BLOCKS.get(level.dimension());
        if (weakened == null) {
            return;
        }

        long now = level.getGameTime();
        weakened.entrySet().removeIf(entry ->
                entry.getValue() <= now || !isWeakenableBedrock(level.getBlockState(entry.getKey())));
        if (weakened.isEmpty()) {
            WEAKENED_BLOCKS.remove(level.dimension());
        }
    }

    private static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        if (event.getEntity() instanceof ServerPlayer player
                && event.getLevel() instanceof ServerLevel level
                && isWeakenableBedrock(event.getTargetBlock())
                && isWeakened(level, event.getPos())
                && canUseToolForWeakenedBedrock(player.getMainHandItem())) {
            event.setCanHarvest(true);
        }
    }

    private static void onBlockDrops(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof ServerPlayer)
                || !isWeakenableBedrock(event.getState())
                || !isWeakened(event.getLevel(), event.getPos())
                || !canUseToolForWeakenedBedrock(event.getTool())) {
            return;
        }

        event.getDrops().clear();
        event.setDroppedExperience(0);
        if (!hasSilkTouch(event.getTool(), event.getLevel())
                || !event.getLevel().getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            return;
        }

        Item blockItem = event.getState().getBlock().asItem();
        if (blockItem == Items.AIR) {
            return;
        }

        BlockPos pos = event.getPos();
        ItemEntity drop = new ItemEntity(event.getLevel(),
                pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                new ItemStack(blockItem));
        drop.setDefaultPickUpDelay();
        event.getDrops().add(drop);
    }

    private static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            clearMining(player);
        }
    }

    private static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            clearMining(player);
        }
    }

    private static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getOriginal() instanceof ServerPlayer player) {
            clearMining(player);
        } else {
            ACTIVE_MINING.remove(event.getOriginal().getUUID());
        }
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        WEAKENED_BLOCKS.clear();
        ACTIVE_MINING.clear();
    }

    private static int getWeakeningDuration(PotionContents contents) {
        int duration = 0;
        for (var effect : contents.getAllEffects()) {
            if (effect.getEffect().is(EgToolsMobEffects.BEDROCK_WEAKENING.getKey())) {
                duration = Math.max(duration, effect.getDuration());
            }
        }
        return duration;
    }

    private static BlockPos impactCenter(HitResult result) {
        if (result instanceof BlockHitResult blockHit) {
            return blockHit.getBlockPos();
        }
        return BlockPos.containing(result.getLocation());
    }

    private static void weakenBlocks(ServerLevel level, BlockPos center, int duration) {
        long expiresAt = level.getGameTime() + duration;
        Map<BlockPos, Long> weakened = WEAKENED_BLOCKS.computeIfAbsent(level.dimension(), ignored -> new HashMap<>());

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-SPLASH_RADIUS, -SPLASH_RADIUS, -SPLASH_RADIUS),
                center.offset(SPLASH_RADIUS, SPLASH_RADIUS, SPLASH_RADIUS))) {
            if (!isWithinSplashRadius(center, pos)) {
                continue;
            }

            BlockState state = level.getBlockState(pos);
            if (isWeakenableBedrock(state)) {
                weakened.merge(pos.immutable(), expiresAt, Math::max);
            }
        }
    }

    private static boolean isWithinSplashRadius(BlockPos center, BlockPos pos) {
        int x = pos.getX() - center.getX();
        int y = pos.getY() - center.getY();
        int z = pos.getZ() - center.getZ();
        return x * x + y * y + z * z <= SPLASH_RADIUS * SPLASH_RADIUS;
    }

    private static boolean isWeakenableBedrock(BlockState state) {
        return state.is(EgToolsTags.Blocks.WEAKENABLE_BEDROCK);
    }

    private static boolean isWeakened(BlockGetter getter, BlockPos pos) {
        if (!(getter instanceof Level level)) {
            return false;
        }

        Map<BlockPos, Long> weakened = WEAKENED_BLOCKS.get(level.dimension());
        if (weakened == null) {
            return false;
        }

        Long expiresAt = weakened.get(pos);
        if (expiresAt == null) {
            return false;
        }

        if (expiresAt <= level.getGameTime()) {
            weakened.remove(pos);
            if (weakened.isEmpty()) {
                WEAKENED_BLOCKS.remove(level.dimension());
            }
            return false;
        }

        return true;
    }

    private static boolean canMineWeakenedBedrock(ServerPlayer player, ServerLevel level, BlockPos pos) {
        return !player.isCreative()
                && !player.isSpectator()
                && player.canInteractWithBlock(pos, 1.0D)
                && !level.isOutsideBuildHeight(pos)
                && isWeakenableBedrock(level.getBlockState(pos))
                && isWeakened(level, pos)
                && level.mayInteract(player, pos)
                && !player.blockActionRestricted(level, pos, player.gameMode.getGameModeForPlayer())
                && canUseToolForWeakenedBedrock(player.getMainHandItem());
    }

    private static boolean canUseToolForWeakenedBedrock(ItemStack tool) {
        return tool.is(Items.NETHERITE_PICKAXE) || tool.is(EgToolsItems.NETHERITE_PAXEL.get());
    }

    private static void startMining(ServerPlayer player, ServerLevel level, BlockPos pos) {
        MiningProgress current = ACTIVE_MINING.get(player.getUUID());
        if (current != null && current.matches(level, pos)) {
            return;
        }

        clearMining(player);
        MiningProgress progress = new MiningProgress(level.dimension(), pos.immutable());
        ACTIVE_MINING.put(player.getUUID(), progress);
        level.destroyBlockProgress(customBreakerId(player), pos, 0);
    }

    private static void tickMining(ServerPlayer player) {
        MiningProgress progress = ACTIVE_MINING.get(player.getUUID());
        if (progress == null) {
            return;
        }

        ServerLevel level = player.server.getLevel(progress.dimension);
        if (level == null || player.level() != level || !player.canInteractWithBlock(progress.pos, 1.0D)) {
            clearMining(player);
            return;
        }

        if (!canMineWeakenedBedrock(player, level, progress.pos)) {
            clearMining(player);
            return;
        }

        progress.progress += getMiningIncrement(player, level, progress.pos);
        if (progress.progress >= 1.0F) {
            ACTIVE_MINING.remove(player.getUUID());
            level.destroyBlockProgress(customBreakerId(player), progress.pos, -1);
            if (!player.gameMode.destroyBlock(progress.pos) || isWeakenableBedrock(level.getBlockState(progress.pos))) {
                player.connection.send(new ClientboundBlockUpdatePacket(progress.pos, level.getBlockState(progress.pos)));
            }
            return;
        }

        int stage = Math.min(9, (int)(progress.progress * 10.0F));
        if (stage != progress.lastStage) {
            level.destroyBlockProgress(customBreakerId(player), progress.pos, stage);
            progress.lastStage = stage;
        }
    }

    private static float getMiningIncrement(ServerPlayer player, ServerLevel level, BlockPos pos) {
        float digSpeed = getWeakenedBedrockDigSpeed(player, level.getBlockState(pos), pos);
        return Math.max(0.0F, digSpeed / WEAKENED_BEDROCK_HARDNESS / 30.0F);
    }

    private static float getWeakenedBedrockDigSpeed(ServerPlayer player, BlockState state, BlockPos pos) {
        float speed = player.getInventory().getDestroySpeed(MINING_SURROGATE);
        if (speed > 1.0F) {
            speed += (float)player.getAttributeValue(Attributes.MINING_EFFICIENCY);
        }

        if (MobEffectUtil.hasDigSpeed(player)) {
            speed *= 1.0F + (float)(MobEffectUtil.getDigSpeedAmplification(player) + 1) * 0.2F;
        }

        if (player.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            speed *= switch (player.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
                case 0 -> 0.3F;
                case 1 -> 0.09F;
                case 2 -> 0.0027F;
                default -> 8.1E-4F;
            };
        }

        speed *= (float)player.getAttributeValue(Attributes.BLOCK_BREAK_SPEED);
        if (player.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value())) {
            speed *= (float)player.getAttributeValue(Attributes.SUBMERGED_MINING_SPEED);
        }

        if (!player.onGround()) {
            speed /= 5.0F;
        }

        return EventHooks.getBreakSpeed(player, state, speed, pos);
    }

    private static boolean clearMining(ServerPlayer player) {
        MiningProgress progress = ACTIVE_MINING.remove(player.getUUID());
        if (progress == null) {
            return false;
        }

        ServerLevel level = player.server.getLevel(progress.dimension);
        if (level != null) {
            level.destroyBlockProgress(customBreakerId(player), progress.pos, -1);
        }
        return true;
    }

    private static int customBreakerId(ServerPlayer player) {
        return -player.getId() - 1;
    }

    private static boolean hasSilkTouch(ItemStack tool, ServerLevel level) {
        if (tool.isEmpty()) {
            return false;
        }

        var enchantments = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var silkTouch = enchantments.getOrThrow(Enchantments.SILK_TOUCH);
        return tool.getEnchantmentLevel(silkTouch) > 0;
    }

    private static final class MiningProgress {
        private final ResourceKey<Level> dimension;
        private final BlockPos pos;
        private float progress;
        private int lastStage = -1;

        private MiningProgress(ResourceKey<Level> dimension, BlockPos pos) {
            this.dimension = dimension;
            this.pos = pos;
        }

        private boolean matches(ServerLevel level, BlockPos pos) {
            return this.dimension.equals(level.dimension()) && this.pos.equals(pos);
        }
    }
}

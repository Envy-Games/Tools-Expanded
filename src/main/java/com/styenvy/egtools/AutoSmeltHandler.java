package com.styenvy.egtools;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

import java.util.ListIterator;
import java.util.Optional;

@EventBusSubscriber(modid = EgTools.MODID)
public final class AutoSmeltHandler {
    private AutoSmeltHandler() {}

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        if (event.isCanceled()) {
            return;
        }

        ServerLevel level = event.getLevel();
        ItemStack tool = event.getTool();
        if (tool.isEmpty()
                || !EnchantmentHelper.has(tool, EgToolsEnchantmentEffectComponents.AUTO_SMELT.get())
                || hasSilkTouch(level, tool)
                || !isOre(event.getState())) {
            return;
        }

        int smeltingExperience = 0;
        ListIterator<ItemEntity> iterator = event.getDrops().listIterator();
        while (iterator.hasNext()) {
            ItemEntity entity = iterator.next();
            SmeltingResult result = smeltDrop(level, entity.getItem());
            if (result != null) {
                entity.setItem(result.stack());
                smeltingExperience += result.experience();
            }
        }

        if (smeltingExperience > 0) {
            event.setDroppedExperience(event.getDroppedExperience() + smeltingExperience);
        }
    }

    private static boolean hasSilkTouch(ServerLevel level, ItemStack tool) {
        var silkTouch = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
        return tool.getEnchantmentLevel(silkTouch) > 0;
    }

    private static boolean isOre(BlockState state) {
        return state.is(Tags.Blocks.ORES);
    }

    private static SmeltingResult smeltDrop(ServerLevel level, ItemStack drop) {
        if (drop.isEmpty()) {
            return null;
        }

        Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(drop), level);
        if (recipe.isEmpty()) {
            return null;
        }

        ItemStack recipeResult = recipe.get().value().assemble(new SingleRecipeInput(drop), level.registryAccess());
        if (recipeResult.isEmpty() || !recipeResult.is(Tags.Items.INGOTS)) {
            return null;
        }

        ItemStack smelted = recipeResult.copyWithCount(drop.getCount() * recipeResult.getCount());
        int experience = getSmeltingExperience(level, drop.getCount(), recipe.get().value().getExperience());
        return new SmeltingResult(smelted, experience);
    }

    private static int getSmeltingExperience(ServerLevel level, int inputCount, float experience) {
        float total = inputCount * experience;
        int whole = Mth.floor(total);
        float fraction = Mth.frac(total);
        return fraction > 0.0F && level.random.nextFloat() < fraction ? whole + 1 : whole;
    }

    private record SmeltingResult(ItemStack stack, int experience) {}
}

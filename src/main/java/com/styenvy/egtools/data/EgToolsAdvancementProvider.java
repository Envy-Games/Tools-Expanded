package com.styenvy.egtools.data;

import com.styenvy.egtools.EgTools;
import com.styenvy.egtools.EgToolsItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public final class EgToolsAdvancementProvider implements AdvancementProvider.AdvancementGenerator {
    private static final ResourceLocation STONE_BACKGROUND =
            ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png");

    @Override
    public void generate(HolderLookup.Provider registries,
                         Consumer<AdvancementHolder> saver,
                         ExistingFileHelper existingFileHelper) {
        AdvancementHolder discoverIron = Advancement.Builder.recipeAdvancement()
                .display(Items.IRON_INGOT,
                        Component.translatable("advancement.egtools.discover_iron.title"),
                        Component.translatable("advancement.egtools.discover_iron.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("has_iron", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
                .rewards(AdvancementRewards.Builder.recipe(id("empty_paint_bucket")))
                .save(saver, id("recipes/tools/discover_iron"), existingFileHelper);

        Advancement.Builder.recipeAdvancement()
                .parent(discoverIron)
                .display(Items.RED_DYE,
                        Component.translatable("advancement.egtools.discover_dye.title"),
                        Component.translatable("advancement.egtools.discover_dye.description"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false)
                .addCriterion("has_any_dye", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(Tags.Items.DYES)))
                .rewards(paintRecipeRewards())
                .save(saver, id("recipes/tools/discover_dye"), existingFileHelper);

        Advancement.Builder.recipeAdvancement()
                .parent(discoverIron)
                .display(EgToolsItems.PAINT_BRUSH.get(),
                        Component.translatable("advancement.egtools.unlock_paint_brush.title"),
                        Component.translatable("advancement.egtools.unlock_paint_brush.description"),
                        STONE_BACKGROUND,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("made_empty_paint_bucket",
                        InventoryChangeTrigger.TriggerInstance.hasItems(EgToolsItems.EMPTY_PAINT_BUCKET.get()))
                .rewards(AdvancementRewards.Builder.recipe(id("paint_brush")).addRecipe(id("paint_brush_charging")))
                .save(saver, id("recipes/tools/unlock_paint_brush"), existingFileHelper);

        Advancement.Builder.recipeAdvancement()
                .parent(AdvancementSubProvider.createPlaceholder("minecraft:story/root"))
                .display(Items.CRAFTING_TABLE,
                        Component.translatable("advancement.egtools.construction_hammer_unlock.title"),
                        Component.translatable("advancement.egtools.construction_hammer_unlock.description"),
                        STONE_BACKGROUND,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .addCriterion("got_crafting_table", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CRAFTING_TABLE))
                .rewards(AdvancementRewards.Builder.recipe(id("construction_hammer")))
                .save(saver, id("recipes/tools/construction_hammer_unlock"), existingFileHelper);
    }

    private static AdvancementRewards.Builder paintRecipeRewards() {
        return AdvancementRewards.Builder.recipe(id("white_paint_bucket"))
                .addRecipe(id("light_gray_paint_bucket"))
                .addRecipe(id("gray_paint_bucket"))
                .addRecipe(id("black_paint_bucket"))
                .addRecipe(id("brown_paint_bucket"))
                .addRecipe(id("red_paint_bucket"))
                .addRecipe(id("orange_paint_bucket"))
                .addRecipe(id("yellow_paint_bucket"))
                .addRecipe(id("lime_paint_bucket"))
                .addRecipe(id("green_paint_bucket"))
                .addRecipe(id("cyan_paint_bucket"))
                .addRecipe(id("light_blue_paint_bucket"))
                .addRecipe(id("blue_paint_bucket"))
                .addRecipe(id("purple_paint_bucket"))
                .addRecipe(id("magenta_paint_bucket"))
                .addRecipe(id("pink_paint_bucket"));
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(EgTools.MODID, path);
    }
}

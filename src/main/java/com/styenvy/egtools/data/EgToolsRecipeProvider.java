package com.styenvy.egtools.data;

import com.styenvy.egtools.EgTools;
import com.styenvy.egtools.EgToolsItems;
import com.styenvy.egtools.PaintBrushRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EgToolsRecipeProvider extends RecipeProvider {
    private static final List<PaintRecipe> PAINT_RECIPES = List.of(
            new PaintRecipe(Items.WHITE_DYE, EgToolsItems.WHITE_PAINT_BUCKET),
            new PaintRecipe(Items.LIGHT_GRAY_DYE, EgToolsItems.LIGHT_GRAY_PAINT_BUCKET),
            new PaintRecipe(Items.GRAY_DYE, EgToolsItems.GRAY_PAINT_BUCKET),
            new PaintRecipe(Items.BLACK_DYE, EgToolsItems.BLACK_PAINT_BUCKET),
            new PaintRecipe(Items.BROWN_DYE, EgToolsItems.BROWN_PAINT_BUCKET),
            new PaintRecipe(Items.RED_DYE, EgToolsItems.RED_PAINT_BUCKET),
            new PaintRecipe(Items.ORANGE_DYE, EgToolsItems.ORANGE_PAINT_BUCKET),
            new PaintRecipe(Items.YELLOW_DYE, EgToolsItems.YELLOW_PAINT_BUCKET),
            new PaintRecipe(Items.LIME_DYE, EgToolsItems.LIME_PAINT_BUCKET),
            new PaintRecipe(Items.GREEN_DYE, EgToolsItems.GREEN_PAINT_BUCKET),
            new PaintRecipe(Items.CYAN_DYE, EgToolsItems.CYAN_PAINT_BUCKET),
            new PaintRecipe(Items.LIGHT_BLUE_DYE, EgToolsItems.LIGHT_BLUE_PAINT_BUCKET),
            new PaintRecipe(Items.BLUE_DYE, EgToolsItems.BLUE_PAINT_BUCKET),
            new PaintRecipe(Items.PURPLE_DYE, EgToolsItems.PURPLE_PAINT_BUCKET),
            new PaintRecipe(Items.MAGENTA_DYE, EgToolsItems.MAGENTA_PAINT_BUCKET),
            new PaintRecipe(Items.PINK_DYE, EgToolsItems.PINK_PAINT_BUCKET)
    );

    public EgToolsRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        RecipeOutput customUnlockedRecipes = new RecipeOnlyOutput(recipeOutput);

        paxel(recipeOutput, EgToolsItems.IRON_PAXEL.get(), Items.IRON_PICKAXE, Items.IRON_AXE,
                Items.IRON_SHOVEL, Items.IRON_HOE, Items.IRON_SWORD);
        paxel(recipeOutput, EgToolsItems.DIAMOND_PAXEL.get(), Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE,
                Items.DIAMOND_SHOVEL, Items.DIAMOND_HOE, Items.DIAMOND_SWORD);
        netheritePaxel(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EgToolsItems.CONSTRUCTION_HAMMER.get())
                .define('I', Items.IRON_BLOCK)
                .define('D', Items.DIAMOND_BLOCK)
                .define('S', Items.STICK)
                .pattern("IDI")
                .pattern(" S ")
                .pattern(" S ")
                .unlockedBy("has_crafting_table", has(Items.CRAFTING_TABLE))
                .save(customUnlockedRecipes);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EgToolsItems.PAINT_BRUSH.get())
                .define('W', ItemTags.WOOL)
                .define('S', Items.STICK)
                .pattern("WWW")
                .pattern("S  ")
                .pattern("S  ")
                .unlockedBy("has_wool", has(ItemTags.WOOL))
                .save(customUnlockedRecipes);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EgToolsItems.EMPTY_PAINT_BUCKET.get())
                .define('I', Items.IRON_INGOT)
                .pattern("I I")
                .pattern("I I")
                .pattern("III")
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(customUnlockedRecipes);

        for (PaintRecipe paintRecipe : PAINT_RECIPES) {
            paintBucket(customUnlockedRecipes, paintRecipe);
        }

        SpecialRecipeBuilder.special(PaintBrushRecipe::new)
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(EgTools.MODID, "paint_brush_charging"));
    }

    private static void paxel(RecipeOutput recipeOutput, ItemLike result, ItemLike pickaxe, ItemLike axe,
                              ItemLike shovel, ItemLike hoe, ItemLike sword) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, result)
                .define('P', pickaxe)
                .define('A', axe)
                .define('S', shovel)
                .define('H', hoe)
                .define('W', sword)
                .pattern("PAS")
                .pattern(" H ")
                .pattern(" W ")
                .unlockedBy("has_pickaxe", has(pickaxe))
                .unlockedBy("has_axe", has(axe))
                .unlockedBy("has_shovel", has(shovel))
                .save(recipeOutput);
    }

    private static void netheritePaxel(RecipeOutput recipeOutput) {
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(EgToolsItems.DIAMOND_PAXEL.get()),
                        Ingredient.of(Items.NETHERITE_INGOT),
                        RecipeCategory.TOOLS,
                        EgToolsItems.NETHERITE_PAXEL.get()
                )
                .unlocks("has_diamond_paxel", has(EgToolsItems.DIAMOND_PAXEL.get()))
                .unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(EgTools.MODID, "netherite_paxel"));
    }

    private static void paintBucket(RecipeOutput recipeOutput, PaintRecipe paintRecipe) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, paintRecipe.bucket().get())
                .requires(paintRecipe.dye())
                .requires(EgToolsItems.EMPTY_PAINT_BUCKET.get())
                .requires(Items.WATER_BUCKET)
                .unlockedBy("has_dye", has(paintRecipe.dye()))
                .unlockedBy("has_empty_paint_bucket", has(EgToolsItems.EMPTY_PAINT_BUCKET.get()))
                .save(recipeOutput);
    }

    private record PaintRecipe(Item dye, DeferredHolder<Item, ? extends Item> bucket) {}

    private record RecipeOnlyOutput(RecipeOutput delegate) implements RecipeOutput {
        @Override
        public void accept(ResourceLocation id, Recipe<?> recipe, AdvancementHolder advancement, ICondition... conditions) {
            delegate.accept(id, recipe, null, conditions);
        }

        @Override
        public Advancement.Builder advancement() {
            return delegate.advancement();
        }
    }
}

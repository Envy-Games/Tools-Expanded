package com.styenvy.egtools;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

@JeiPlugin
public final class EgToolsJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_UID = ResourceLocation.fromNamespaceAndPath(EgTools.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(PaintBrushRecipe.class, new PaintBrushChargingExtension());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addItemStackInfo(
                EgToolsItems.PAINT_BRUSH.get().getDefaultInstance(),
                Component.translatable("jei.egtools.paint_brush.info")
        );
        registration.addItemStackInfo(
                weakeningPotionStacks(),
                Component.translatable("jei.egtools.weakening_potion.info")
        );
        registration.addItemStackInfo(
                Blocks.BEDROCK.asItem().getDefaultInstance(),
                Component.translatable("jei.egtools.weakenable_bedrock.info")
        );
    }

    private static final class PaintBrushChargingExtension implements ICraftingCategoryExtension<PaintBrushRecipe> {
        @Override
        public void setRecipe(RecipeHolder<PaintBrushRecipe> recipeHolder,
                              IRecipeLayoutBuilder builder,
                              ICraftingGridHelper craftingGridHelper,
                              IFocusGroup focuses) {
            craftingGridHelper.createAndSetInputs(builder, List.of(
                    List.of(EgToolsItems.PAINT_BRUSH.get().getDefaultInstance()),
                    paintBuckets()
            ), 0, 0);
            craftingGridHelper.createAndSetOutputs(builder, chargedBrushes());
            builder.setShapeless();
        }
    }

    private static List<ItemStack> paintBuckets() {
        return List.of(
                EgToolsItems.WHITE_PAINT_BUCKET.get().getDefaultInstance(),
                EgToolsItems.LIGHT_GRAY_PAINT_BUCKET.get().getDefaultInstance(),
                EgToolsItems.GRAY_PAINT_BUCKET.get().getDefaultInstance(),
                EgToolsItems.BLACK_PAINT_BUCKET.get().getDefaultInstance(),
                EgToolsItems.BROWN_PAINT_BUCKET.get().getDefaultInstance(),
                EgToolsItems.RED_PAINT_BUCKET.get().getDefaultInstance(),
                EgToolsItems.ORANGE_PAINT_BUCKET.get().getDefaultInstance(),
                EgToolsItems.YELLOW_PAINT_BUCKET.get().getDefaultInstance(),
                EgToolsItems.LIME_PAINT_BUCKET.get().getDefaultInstance(),
                EgToolsItems.GREEN_PAINT_BUCKET.get().getDefaultInstance(),
                EgToolsItems.CYAN_PAINT_BUCKET.get().getDefaultInstance(),
                EgToolsItems.LIGHT_BLUE_PAINT_BUCKET.get().getDefaultInstance(),
                EgToolsItems.BLUE_PAINT_BUCKET.get().getDefaultInstance(),
                EgToolsItems.PURPLE_PAINT_BUCKET.get().getDefaultInstance(),
                EgToolsItems.MAGENTA_PAINT_BUCKET.get().getDefaultInstance(),
                EgToolsItems.PINK_PAINT_BUCKET.get().getDefaultInstance()
        );
    }

    private static List<ItemStack> chargedBrushes() {
        return List.of(
                chargedBrush(DyeColor.WHITE),
                chargedBrush(DyeColor.LIGHT_GRAY),
                chargedBrush(DyeColor.GRAY),
                chargedBrush(DyeColor.BLACK),
                chargedBrush(DyeColor.BROWN),
                chargedBrush(DyeColor.RED),
                chargedBrush(DyeColor.ORANGE),
                chargedBrush(DyeColor.YELLOW),
                chargedBrush(DyeColor.LIME),
                chargedBrush(DyeColor.GREEN),
                chargedBrush(DyeColor.CYAN),
                chargedBrush(DyeColor.LIGHT_BLUE),
                chargedBrush(DyeColor.BLUE),
                chargedBrush(DyeColor.PURPLE),
                chargedBrush(DyeColor.MAGENTA),
                chargedBrush(DyeColor.PINK)
        );
    }

    private static ItemStack chargedBrush(DyeColor color) {
        ItemStack stack = EgToolsItems.PAINT_BRUSH.get().getDefaultInstance();
        PaintBrushItem.chargeBrush(stack, color, PaintBucketItem.MAX_PAINTS);
        return stack;
    }

    private static List<ItemStack> weakeningPotionStacks() {
        return List.of(
                PotionContents.createItemStack(Items.POTION, EgToolsPotions.WEAKENING),
                PotionContents.createItemStack(Items.SPLASH_POTION, EgToolsPotions.WEAKENING),
                PotionContents.createItemStack(Items.LINGERING_POTION, EgToolsPotions.WEAKENING),
                PotionContents.createItemStack(Items.TIPPED_ARROW, EgToolsPotions.WEAKENING)
        );
    }
}

package com.styenvy.egtools;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EgToolsRecipeSerializers {
    private EgToolsRecipeSerializers() {}

    public static final DeferredRegister<RecipeSerializer<?>> REGISTER =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, EgTools.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<PaintBrushRecipe>>
            PAINT_BRUSH_RECIPE = REGISTER.register("paint_brush_charging",
            () -> new SimpleCraftingRecipeSerializer<>(PaintBrushRecipe::new));
}
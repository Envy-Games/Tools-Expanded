package com.styenvy.egtools.data;

import com.styenvy.egtools.EgTools;
import com.styenvy.egtools.EgToolsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class EgToolsItemTagsProvider extends ItemTagsProvider {
    private static final TagKey<Item> TOOLS = itemTag(EgTools.MODID, "tools");
    private static final TagKey<Item> SWORDS = itemTag(EgTools.MODID, "swords");
    private static final TagKey<Item> AREA_MINING_ENCHANTABLE = itemTag(EgTools.MODID, "enchantable/area_mining");

    public EgToolsItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                   ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CompletableFuture.completedFuture(TagsProvider.TagLookup.empty()), EgTools.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(TOOLS).add(
                EgToolsItems.IRON_PAXEL.get(),
                EgToolsItems.DIAMOND_PAXEL.get(),
                EgToolsItems.NETHERITE_PAXEL.get()
        );

        tag(SWORDS).add(
                EgToolsItems.IRON_PAXEL.get(),
                EgToolsItems.DIAMOND_PAXEL.get(),
                EgToolsItems.NETHERITE_PAXEL.get()
        );

        tag(AREA_MINING_ENCHANTABLE)
                .addTag(ItemTags.PICKAXES)
                .add(
                        EgToolsItems.IRON_PAXEL.get(),
                        EgToolsItems.DIAMOND_PAXEL.get(),
                        EgToolsItems.NETHERITE_PAXEL.get()
                );

        addPaxelsTo(ItemTags.MINING_ENCHANTABLE);
        addPaxelsTo(ItemTags.MINING_LOOT_ENCHANTABLE);
        addPaxelsTo(ItemTags.DURABILITY_ENCHANTABLE);
        addPaxelsTo(ItemTags.SWORD_ENCHANTABLE);
        addPaxelsTo(ItemTags.SHARP_WEAPON_ENCHANTABLE);
    }

    private void addPaxelsTo(TagKey<Item> tag) {
        tag(tag).add(
                EgToolsItems.IRON_PAXEL.get(),
                EgToolsItems.DIAMOND_PAXEL.get(),
                EgToolsItems.NETHERITE_PAXEL.get()
        );
    }

    private static TagKey<Item> itemTag(String namespace, String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}

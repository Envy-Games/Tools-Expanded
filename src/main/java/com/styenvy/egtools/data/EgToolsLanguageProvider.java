package com.styenvy.egtools.data;

import com.styenvy.egtools.EgTools;
import com.styenvy.egtools.EgToolsItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class EgToolsLanguageProvider extends LanguageProvider {
    public EgToolsLanguageProvider(PackOutput output) {
        super(output, EgTools.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addItem(EgToolsItems.IRON_PAXEL, "Iron Paxel");
        addItem(EgToolsItems.DIAMOND_PAXEL, "Diamond Paxel");
        addItem(EgToolsItems.NETHERITE_PAXEL, "Netherite Paxel");
        addItem(EgToolsItems.CONSTRUCTION_HAMMER, "Construction Hammer");
        addItem(EgToolsItems.PAINT_BRUSH, "Paint Brush");
        addItem(EgToolsItems.EMPTY_PAINT_BUCKET, "Empty Paint Bucket");
        addItem(EgToolsItems.WHITE_PAINT_BUCKET, "White Paint Bucket");
        addItem(EgToolsItems.LIGHT_GRAY_PAINT_BUCKET, "Light Gray Paint Bucket");
        addItem(EgToolsItems.GRAY_PAINT_BUCKET, "Gray Paint Bucket");
        addItem(EgToolsItems.BLACK_PAINT_BUCKET, "Black Paint Bucket");
        addItem(EgToolsItems.BROWN_PAINT_BUCKET, "Brown Paint Bucket");
        addItem(EgToolsItems.RED_PAINT_BUCKET, "Red Paint Bucket");
        addItem(EgToolsItems.ORANGE_PAINT_BUCKET, "Orange Paint Bucket");
        addItem(EgToolsItems.YELLOW_PAINT_BUCKET, "Yellow Paint Bucket");
        addItem(EgToolsItems.LIME_PAINT_BUCKET, "Lime Paint Bucket");
        addItem(EgToolsItems.GREEN_PAINT_BUCKET, "Green Paint Bucket");
        addItem(EgToolsItems.CYAN_PAINT_BUCKET, "Cyan Paint Bucket");
        addItem(EgToolsItems.LIGHT_BLUE_PAINT_BUCKET, "Light Blue Paint Bucket");
        addItem(EgToolsItems.BLUE_PAINT_BUCKET, "Blue Paint Bucket");
        addItem(EgToolsItems.PURPLE_PAINT_BUCKET, "Purple Paint Bucket");
        addItem(EgToolsItems.MAGENTA_PAINT_BUCKET, "Magenta Paint Bucket");
        addItem(EgToolsItems.PINK_PAINT_BUCKET, "Pink Paint Bucket");

        add("itemGroup.egtools", "Tools Expanded");
        add("effect.egtools.bedrock_weakening", "Weakening");
        add("item.minecraft.potion.effect.weakening", "Potion of Weakening");
        add("item.minecraft.splash_potion.effect.weakening", "Splash Potion of Weakening");
        add("item.minecraft.lingering_potion.effect.weakening", "Lingering Potion of Weakening");
        add("item.minecraft.tipped_arrow.effect.weakening", "Arrow of Weakening");
        add("item.egtools.construction_hammer.tooltip1", "Left-Click: Cycle block variants forward");
        add("item.egtools.construction_hammer.tooltip2", "Shift+Left-Click: Cycle block variants backward");
        add("item.egtools.construction_hammer.tooltip3", "Cycles through building block variants");
        add("item.egtools.paxel.tooltip1", "Acts as a pickaxe, axe, shovel, hoe, shears & sword");
        add("item.egtools.paxel.tooltip2", "Right-Click: strip, scrape, unwax, flatten, douse, till, trim & shear");
        add("item.egtools.paxel.tooltip3", "Can perform sword sweep attacks");
        add("item.egtools.paxel.tooltip4", "Supports mining, durability & sword enchantments");
        add("item.egtools.paint_bucket.tooltip", "Contains 32 paints");
        add("item.egtools.paint_bucket.remaining", "Remaining: %s / %s");
        add("item.egtools.paint_brush.tooltip.paint", "Paint: %s");
        add("item.egtools.paint_brush.tooltip.uses", "Uses: %s / %s");
        add("item.egtools.paint_brush.tooltip.empty", "No paint loaded");
        add("item.egtools.paint_brush.tooltip.charge", "Combine with paint bucket to charge");
        add("item.egtools.paint_brush.tooltip.use", "Left-click to paint blocks");
        add("message.egtools.paint_brush.no_paint", "Brush has no paint!");
        add("message.egtools.paint_brush.not_paintable", "This block cannot be painted");
        add("message.egtools.paint_brush.same_color", "Block is already this color");
        add("advancement.egtools.discover_iron.title", "A Shiny Discovery");
        add("advancement.egtools.discover_iron.description", "Find iron ingots to learn how to shape an empty paint bucket.");
        add("advancement.egtools.discover_dye.title", "Color Theory");
        add("advancement.egtools.discover_dye.description", "Find any dye to learn paint mixing.");
        add("advancement.egtools.unlock_paint_brush.title", "First Coat");
        add("advancement.egtools.unlock_paint_brush.description", "Craft an Empty Paint Bucket to learn how to make a Paint Brush.");
        add("advancement.egtools.construction_hammer_unlock.title", "Workbench Ready");
        add("advancement.egtools.construction_hammer_unlock.description", "You made a Crafting Table; Construction Hammer recipe unlocked.");
        add("enchantment.egtools.excavation", "Excavation");
        add("enchantment.egtools.quarrying", "Quarrying");
        add("enchantment.egtools.auto_smelt", "Auto Smelt");
        add("jei.egtools.paint_brush.info", "Combine a paint brush with any paint bucket in a crafting grid to load paint. JEI displays every paint color as a rotating shapeless recipe.");
        add("jei.egtools.weakening_potion.info", "Brew with Awkward Potion and Crying Obsidian, then add Gunpowder for the throwable Splash Potion of Weakening. The splash weakens nearby blocks in the egtools:weakenable_bedrock tag for a short time.");
        add("jei.egtools.weakenable_bedrock.info", "Blocks in the egtools:weakenable_bedrock tag can be mined only while weakened by the splash potion. A netherite pickaxe or Netherite Paxel is required; Silk Touch drops one block, while Fortune has no effect.");
    }
}

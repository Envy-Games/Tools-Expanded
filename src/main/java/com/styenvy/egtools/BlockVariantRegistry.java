package com.styenvy.egtools;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Registry for block variant cycles.
 * Provides O(1) lookup for finding which cycle a block belongs to.
 * This registry contains all building block families in Minecraft 1.21.1,
 * excluding cosmetic blocks, monster drops, and colored blocks.
 */
public final class BlockVariantRegistry {
    private static final Map<Block, BlockVariantCycle> blockToCycle = new HashMap<>();
    private static final List<BlockVariantCycle> allCycles = new ArrayList<>();

    static {
        initializeRegistry();
    }

    private BlockVariantRegistry() {} // Prevent instantiation

    /**
     * Gets the variant cycle for a given block.
     *
     * @param block The block to look up
     * @return The cycle containing this block, or null if not in any cycle
     */
    @Nullable
    public static BlockVariantCycle getCycle(Block block) {
        return blockToCycle.get(block);
    }

    /**
     * Registers a new block variant cycle.
     *
     * @param cycle The cycle to register
     */
    public static void registerCycle(BlockVariantCycle cycle) {
        allCycles.add(cycle);
        for (Block block : cycle.getBlocks()) {
            if (blockToCycle.containsKey(block)) {
                throw new IllegalStateException("Block " + block + " is already registered in another cycle");
            }
            blockToCycle.put(block, cycle);
        }
    }

    /**
     * Gets all registered cycles.
     *
     * @return Unmodifiable list of all cycles
     */
    public static List<BlockVariantCycle> getAllCycles() {
        return Collections.unmodifiableList(allCycles);
    }

    /**
     * Initializes all block variant cycles for Minecraft 1.21.1.
     */
    private static void initializeRegistry() {
        // Wood families
        registerWoodFamily("oak", Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG, Blocks.OAK_WOOD,
                Blocks.STRIPPED_OAK_WOOD, Blocks.OAK_PLANKS, Blocks.OAK_STAIRS, Blocks.OAK_SLAB,
                Blocks.OAK_FENCE, Blocks.OAK_FENCE_GATE, Blocks.OAK_DOOR, Blocks.OAK_TRAPDOOR,
                Blocks.OAK_BUTTON, Blocks.OAK_PRESSURE_PLATE);

        registerWoodFamily("spruce", Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG, Blocks.SPRUCE_WOOD,
                Blocks.STRIPPED_SPRUCE_WOOD, Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_STAIRS, Blocks.SPRUCE_SLAB,
                Blocks.SPRUCE_FENCE, Blocks.SPRUCE_FENCE_GATE, Blocks.SPRUCE_DOOR, Blocks.SPRUCE_TRAPDOOR,
                Blocks.SPRUCE_BUTTON, Blocks.SPRUCE_PRESSURE_PLATE);

        registerWoodFamily("birch", Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG, Blocks.BIRCH_WOOD,
                Blocks.STRIPPED_BIRCH_WOOD, Blocks.BIRCH_PLANKS, Blocks.BIRCH_STAIRS, Blocks.BIRCH_SLAB,
                Blocks.BIRCH_FENCE, Blocks.BIRCH_FENCE_GATE, Blocks.BIRCH_DOOR, Blocks.BIRCH_TRAPDOOR,
                Blocks.BIRCH_BUTTON, Blocks.BIRCH_PRESSURE_PLATE);

        registerWoodFamily("jungle", Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG, Blocks.JUNGLE_WOOD,
                Blocks.STRIPPED_JUNGLE_WOOD, Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_STAIRS, Blocks.JUNGLE_SLAB,
                Blocks.JUNGLE_FENCE, Blocks.JUNGLE_FENCE_GATE, Blocks.JUNGLE_DOOR, Blocks.JUNGLE_TRAPDOOR,
                Blocks.JUNGLE_BUTTON, Blocks.JUNGLE_PRESSURE_PLATE);

        registerWoodFamily("acacia", Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG, Blocks.ACACIA_WOOD,
                Blocks.STRIPPED_ACACIA_WOOD, Blocks.ACACIA_PLANKS, Blocks.ACACIA_STAIRS, Blocks.ACACIA_SLAB,
                Blocks.ACACIA_FENCE, Blocks.ACACIA_FENCE_GATE, Blocks.ACACIA_DOOR, Blocks.ACACIA_TRAPDOOR,
                Blocks.ACACIA_BUTTON, Blocks.ACACIA_PRESSURE_PLATE);

        registerWoodFamily("dark_oak", Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.DARK_OAK_WOOD,
                Blocks.STRIPPED_DARK_OAK_WOOD, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_STAIRS, Blocks.DARK_OAK_SLAB,
                Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_FENCE_GATE, Blocks.DARK_OAK_DOOR, Blocks.DARK_OAK_TRAPDOOR,
                Blocks.DARK_OAK_BUTTON, Blocks.DARK_OAK_PRESSURE_PLATE);

        registerWoodFamily("mangrove", Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG, Blocks.MANGROVE_WOOD,
                Blocks.STRIPPED_MANGROVE_WOOD, Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_STAIRS, Blocks.MANGROVE_SLAB,
                Blocks.MANGROVE_FENCE, Blocks.MANGROVE_FENCE_GATE, Blocks.MANGROVE_DOOR, Blocks.MANGROVE_TRAPDOOR,
                Blocks.MANGROVE_BUTTON, Blocks.MANGROVE_PRESSURE_PLATE);

        registerWoodFamily("cherry", Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG, Blocks.CHERRY_WOOD,
                Blocks.STRIPPED_CHERRY_WOOD, Blocks.CHERRY_PLANKS, Blocks.CHERRY_STAIRS, Blocks.CHERRY_SLAB,
                Blocks.CHERRY_FENCE, Blocks.CHERRY_FENCE_GATE, Blocks.CHERRY_DOOR, Blocks.CHERRY_TRAPDOOR,
                Blocks.CHERRY_BUTTON, Blocks.CHERRY_PRESSURE_PLATE);

        registerWoodFamily("bamboo", Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK, Blocks.BAMBOO_PLANKS,
                Blocks.BAMBOO_STAIRS, Blocks.BAMBOO_SLAB, Blocks.BAMBOO_FENCE, Blocks.BAMBOO_FENCE_GATE,
                Blocks.BAMBOO_DOOR, Blocks.BAMBOO_TRAPDOOR, Blocks.BAMBOO_BUTTON, Blocks.BAMBOO_PRESSURE_PLATE,
                Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_MOSAIC_STAIRS, Blocks.BAMBOO_MOSAIC_SLAB);

        registerWoodFamily("crimson", Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM, Blocks.CRIMSON_HYPHAE,
                Blocks.STRIPPED_CRIMSON_HYPHAE, Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_STAIRS, Blocks.CRIMSON_SLAB,
                Blocks.CRIMSON_FENCE, Blocks.CRIMSON_FENCE_GATE, Blocks.CRIMSON_DOOR, Blocks.CRIMSON_TRAPDOOR,
                Blocks.CRIMSON_BUTTON, Blocks.CRIMSON_PRESSURE_PLATE);

        registerWoodFamily("warped", Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM, Blocks.WARPED_HYPHAE,
                Blocks.STRIPPED_WARPED_HYPHAE, Blocks.WARPED_PLANKS, Blocks.WARPED_STAIRS, Blocks.WARPED_SLAB,
                Blocks.WARPED_FENCE, Blocks.WARPED_FENCE_GATE, Blocks.WARPED_DOOR, Blocks.WARPED_TRAPDOOR,
                Blocks.WARPED_BUTTON, Blocks.WARPED_PRESSURE_PLATE);

        // Stone families
        registerCycle(new BlockVariantCycle("stone",
                Blocks.STONE, Blocks.STONE_STAIRS, Blocks.STONE_SLAB, Blocks.STONE_BUTTON,
                Blocks.STONE_PRESSURE_PLATE, Blocks.STONE_BRICKS, Blocks.STONE_BRICK_STAIRS,
                Blocks.STONE_BRICK_SLAB, Blocks.STONE_BRICK_WALL, Blocks.CHISELED_STONE_BRICKS,
                Blocks.CRACKED_STONE_BRICKS, Blocks.SMOOTH_STONE, Blocks.SMOOTH_STONE_SLAB));

        registerCycle(new BlockVariantCycle("cobblestone",
                Blocks.COBBLESTONE, Blocks.COBBLESTONE_STAIRS, Blocks.COBBLESTONE_SLAB,
                Blocks.COBBLESTONE_WALL, Blocks.MOSSY_COBBLESTONE, Blocks.MOSSY_COBBLESTONE_STAIRS,
                Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE_WALL));

        registerCycle(new BlockVariantCycle("deepslate",
                Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE_STAIRS,
                Blocks.COBBLED_DEEPSLATE_SLAB, Blocks.COBBLED_DEEPSLATE_WALL, Blocks.POLISHED_DEEPSLATE,
                Blocks.POLISHED_DEEPSLATE_STAIRS, Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.POLISHED_DEEPSLATE_WALL,
                Blocks.DEEPSLATE_BRICKS, Blocks.DEEPSLATE_BRICK_STAIRS, Blocks.DEEPSLATE_BRICK_SLAB,
                Blocks.DEEPSLATE_BRICK_WALL, Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_TILE_STAIRS,
                Blocks.DEEPSLATE_TILE_SLAB, Blocks.DEEPSLATE_TILE_WALL, Blocks.CHISELED_DEEPSLATE,
                Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_TILES));

        registerCycle(new BlockVariantCycle("granite",
                Blocks.GRANITE, Blocks.GRANITE_STAIRS, Blocks.GRANITE_SLAB, Blocks.GRANITE_WALL,
                Blocks.POLISHED_GRANITE, Blocks.POLISHED_GRANITE_STAIRS, Blocks.POLISHED_GRANITE_SLAB));

        registerCycle(new BlockVariantCycle("diorite",
                Blocks.DIORITE, Blocks.DIORITE_STAIRS, Blocks.DIORITE_SLAB, Blocks.DIORITE_WALL,
                Blocks.POLISHED_DIORITE, Blocks.POLISHED_DIORITE_STAIRS, Blocks.POLISHED_DIORITE_SLAB));

        registerCycle(new BlockVariantCycle("andesite",
                Blocks.ANDESITE, Blocks.ANDESITE_STAIRS, Blocks.ANDESITE_SLAB, Blocks.ANDESITE_WALL,
                Blocks.POLISHED_ANDESITE, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.POLISHED_ANDESITE_SLAB));

        registerCycle(new BlockVariantCycle("tuff",
                Blocks.TUFF, Blocks.TUFF_STAIRS, Blocks.TUFF_SLAB, Blocks.TUFF_WALL,
                Blocks.POLISHED_TUFF, Blocks.POLISHED_TUFF_STAIRS, Blocks.POLISHED_TUFF_SLAB,
                Blocks.POLISHED_TUFF_WALL, Blocks.TUFF_BRICKS, Blocks.TUFF_BRICK_STAIRS,
                Blocks.TUFF_BRICK_SLAB, Blocks.TUFF_BRICK_WALL, Blocks.CHISELED_TUFF,
                Blocks.CHISELED_TUFF_BRICKS));

        registerCycle(new BlockVariantCycle("sandstone",
                Blocks.SANDSTONE, Blocks.SANDSTONE_STAIRS, Blocks.SANDSTONE_SLAB, Blocks.SANDSTONE_WALL,
                Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_SANDSTONE_STAIRS, Blocks.SMOOTH_SANDSTONE_SLAB,
                Blocks.CUT_SANDSTONE, Blocks.CUT_SANDSTONE_SLAB, Blocks.CHISELED_SANDSTONE));

        registerCycle(new BlockVariantCycle("red_sandstone",
                Blocks.RED_SANDSTONE, Blocks.RED_SANDSTONE_STAIRS, Blocks.RED_SANDSTONE_SLAB,
                Blocks.RED_SANDSTONE_WALL, Blocks.SMOOTH_RED_SANDSTONE, Blocks.SMOOTH_RED_SANDSTONE_STAIRS,
                Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.CUT_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE_SLAB,
                Blocks.CHISELED_RED_SANDSTONE));

        registerCycle(new BlockVariantCycle("brick",
                Blocks.BRICKS, Blocks.BRICK_STAIRS, Blocks.BRICK_SLAB, Blocks.BRICK_WALL));

        registerCycle(new BlockVariantCycle("prismarine",
                Blocks.PRISMARINE, Blocks.PRISMARINE_STAIRS, Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE_WALL,
                Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICK_STAIRS, Blocks.PRISMARINE_BRICK_SLAB,
                Blocks.DARK_PRISMARINE, Blocks.DARK_PRISMARINE_STAIRS, Blocks.DARK_PRISMARINE_SLAB));

        registerCycle(new BlockVariantCycle("nether_brick",
                Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_BRICK_SLAB,
                Blocks.NETHER_BRICK_WALL, Blocks.NETHER_BRICK_FENCE, Blocks.CHISELED_NETHER_BRICKS,
                Blocks.CRACKED_NETHER_BRICKS, Blocks.RED_NETHER_BRICKS, Blocks.RED_NETHER_BRICK_STAIRS,
                Blocks.RED_NETHER_BRICK_SLAB, Blocks.RED_NETHER_BRICK_WALL));

        registerCycle(new BlockVariantCycle("quartz",
                Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_STAIRS, Blocks.QUARTZ_SLAB, Blocks.QUARTZ_PILLAR,
                Blocks.CHISELED_QUARTZ_BLOCK, Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_QUARTZ_STAIRS,
                Blocks.SMOOTH_QUARTZ_SLAB, Blocks.QUARTZ_BRICKS));

        registerCycle(new BlockVariantCycle("purpur",
                Blocks.PURPUR_BLOCK, Blocks.PURPUR_STAIRS, Blocks.PURPUR_SLAB, Blocks.PURPUR_PILLAR));

        registerCycle(new BlockVariantCycle("end_stone",
                Blocks.END_STONE, Blocks.END_STONE_BRICKS, Blocks.END_STONE_BRICK_STAIRS,
                Blocks.END_STONE_BRICK_SLAB, Blocks.END_STONE_BRICK_WALL));

        registerCycle(new BlockVariantCycle("blackstone",
                Blocks.BLACKSTONE, Blocks.BLACKSTONE_STAIRS, Blocks.BLACKSTONE_SLAB, Blocks.BLACKSTONE_WALL,
                Blocks.POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.POLISHED_BLACKSTONE_SLAB,
                Blocks.POLISHED_BLACKSTONE_WALL, Blocks.POLISHED_BLACKSTONE_BUTTON, Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE,
                Blocks.CHISELED_POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE_BRICKS,
                Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB,
                Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS));

        // Copper families - Non-waxed only, separated by oxidation level
        registerCycle(new BlockVariantCycle("copper",
                Blocks.COPPER_BLOCK, Blocks.CUT_COPPER, Blocks.CUT_COPPER_STAIRS, Blocks.CUT_COPPER_SLAB));

        registerCycle(new BlockVariantCycle("exposed_copper",
                Blocks.EXPOSED_COPPER, Blocks.EXPOSED_CUT_COPPER, Blocks.EXPOSED_CUT_COPPER_STAIRS,
                Blocks.EXPOSED_CUT_COPPER_SLAB));

        registerCycle(new BlockVariantCycle("weathered_copper",
                Blocks.WEATHERED_COPPER, Blocks.WEATHERED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER_STAIRS,
                Blocks.WEATHERED_CUT_COPPER_SLAB));

        registerCycle(new BlockVariantCycle("oxidized_copper",
                Blocks.OXIDIZED_COPPER, Blocks.OXIDIZED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER_STAIRS,
                Blocks.OXIDIZED_CUT_COPPER_SLAB));

        // Mud family
        registerCycle(new BlockVariantCycle("mud",
                Blocks.MUD, Blocks.PACKED_MUD, Blocks.MUD_BRICKS, Blocks.MUD_BRICK_STAIRS,
                Blocks.MUD_BRICK_SLAB, Blocks.MUD_BRICK_WALL));

        // Basalt family
        registerCycle(new BlockVariantCycle("basalt",
                Blocks.BASALT, Blocks.POLISHED_BASALT, Blocks.SMOOTH_BASALT));

        // Calcite
        registerCycle(new BlockVariantCycle("calcite", Blocks.CALCITE));

        // Amethyst
        registerCycle(new BlockVariantCycle("amethyst",
                Blocks.AMETHYST_BLOCK, Blocks.BUDDING_AMETHYST));

        // Dripstone
        registerCycle(new BlockVariantCycle("dripstone", Blocks.DRIPSTONE_BLOCK));

        // Obsidian
        registerCycle(new BlockVariantCycle("obsidian",
                Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN));

        // Ice family
        registerCycle(new BlockVariantCycle("ice",
                Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE));

        // Snow family
        registerCycle(new BlockVariantCycle("snow",
                Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW));

        // Netherrack family
        registerCycle(new BlockVariantCycle("netherrack",
                Blocks.NETHERRACK, Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK));

        // Soul family
        registerCycle(new BlockVariantCycle("soul",
                Blocks.SOUL_SAND, Blocks.SOUL_SOIL));

        // Sculk family
        registerCycle(new BlockVariantCycle("sculk",
                Blocks.SCULK, Blocks.SCULK_CATALYST, Blocks.SCULK_SENSOR, Blocks.SCULK_SHRIEKER,
                Blocks.CALIBRATED_SCULK_SENSOR));

        // Moss family
        registerCycle(new BlockVariantCycle("moss",
                Blocks.MOSS_BLOCK, Blocks.MOSS_CARPET));

        // Light sources cycle
        registerCycle(new BlockVariantCycle("torches",
                Blocks.TORCH, Blocks.SOUL_TORCH, Blocks.REDSTONE_TORCH, Blocks.LANTERN, Blocks.SOUL_LANTERN));

        // Froglights cycle
        registerCycle(new BlockVariantCycle("froglights",
                Blocks.OCHRE_FROGLIGHT, Blocks.VERDANT_FROGLIGHT, Blocks.PEARLESCENT_FROGLIGHT));

        // Workstations and utility blocks cycle
        registerCycle(new BlockVariantCycle("workstations",
                Blocks.CRAFTING_TABLE, Blocks.STONECUTTER, Blocks.CARTOGRAPHY_TABLE, Blocks.FLETCHING_TABLE,
                Blocks.SMITHING_TABLE, Blocks.GRINDSTONE, Blocks.LOOM, Blocks.FURNACE, Blocks.SMOKER,
                Blocks.BLAST_FURNACE, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE, Blocks.ANVIL, Blocks.COMPOSTER,
                Blocks.CAULDRON, Blocks.BREWING_STAND));

        // Rails
        registerCycle(new BlockVariantCycle("rails",
                Blocks.RAIL, Blocks.POWERED_RAIL, Blocks.DETECTOR_RAIL, Blocks.ACTIVATOR_RAIL));

        // Droppers and hoppers
        registerCycle(new BlockVariantCycle("dispensers",
                Blocks.DISPENSER, Blocks.DROPPER, Blocks.HOPPER));

        // Pistons
        registerCycle(new BlockVariantCycle("pistons",
                Blocks.PISTON, Blocks.STICKY_PISTON));

        // Redstone components
        registerCycle(new BlockVariantCycle("redstone_components",
                Blocks.REPEATER, Blocks.COMPARATOR));

        // Other building materials
        registerCycle(new BlockVariantCycle("iron",
                Blocks.IRON_BLOCK, Blocks.IRON_BARS, Blocks.IRON_DOOR, Blocks.IRON_TRAPDOOR));

        registerCycle(new BlockVariantCycle("gold", Blocks.GOLD_BLOCK));

        registerCycle(new BlockVariantCycle("diamond", Blocks.DIAMOND_BLOCK));

        registerCycle(new BlockVariantCycle("emerald", Blocks.EMERALD_BLOCK));

        registerCycle(new BlockVariantCycle("lapis", Blocks.LAPIS_BLOCK));

        registerCycle(new BlockVariantCycle("redstone", Blocks.REDSTONE_BLOCK));

        registerCycle(new BlockVariantCycle("netherite", Blocks.NETHERITE_BLOCK));

        registerCycle(new BlockVariantCycle("coal", Blocks.COAL_BLOCK));

        registerCycle(new BlockVariantCycle("hay", Blocks.HAY_BLOCK));

        registerCycle(new BlockVariantCycle("honeycomb", Blocks.HONEYCOMB_BLOCK));

        registerCycle(new BlockVariantCycle("slime", Blocks.SLIME_BLOCK));

        registerCycle(new BlockVariantCycle("honey", Blocks.HONEY_BLOCK));

        registerCycle(new BlockVariantCycle("dried_kelp", Blocks.DRIED_KELP_BLOCK));

        registerCycle(new BlockVariantCycle("bone", Blocks.BONE_BLOCK));

        registerCycle(new BlockVariantCycle("glowstone", Blocks.GLOWSTONE));

        registerCycle(new BlockVariantCycle("sea_lantern", Blocks.SEA_LANTERN));

        registerCycle(new BlockVariantCycle("shroomlight", Blocks.SHROOMLIGHT));

        registerCycle(new BlockVariantCycle("magma", Blocks.MAGMA_BLOCK));

        // Miscellaneous building blocks
        registerCycle(new BlockVariantCycle("dirt",
                Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT, Blocks.DIRT_PATH,
                Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.MYCELIUM));

        registerCycle(new BlockVariantCycle("sand",
                Blocks.SAND, Blocks.RED_SAND));

        registerCycle(new BlockVariantCycle("gravel", Blocks.GRAVEL));

        registerCycle(new BlockVariantCycle("bookshelf",
                Blocks.BOOKSHELF, Blocks.CHISELED_BOOKSHELF));

        registerCycle(new BlockVariantCycle("terracotta", Blocks.TERRACOTTA));
    }

    /**
     * Helper method to register a wood family with standard variants.
     */
    private static void registerWoodFamily(String name, Block... blocks) {
        registerCycle(new BlockVariantCycle(name, blocks));
    }

    /**
     * Clears the registry. Useful for testing or reinitialization.
     */
    public static void clear() {
        blockToCycle.clear();
        allCycles.clear();
    }

    /**
     * Reinitializes the registry with default values.
     */
    public static void reinitialize() {
        clear();
        initializeRegistry();
    }

    /**
     * Gets the total number of blocks registered.
     *
     * @return The number of blocks in the registry
     */
    public static int getRegisteredBlockCount() {
        return blockToCycle.size();
    }

    /**
     * Gets the total number of cycles registered.
     *
     * @return The number of cycles in the registry
     */
    public static int getRegisteredCycleCount() {
        return allCycles.size();
    }
}
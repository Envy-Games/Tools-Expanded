package com.styenvy.egtools;

import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents a cycle of related block variants that can be cycled through.
 * Provides O(1) lookup for next and previous blocks in the cycle.
 */
public class BlockVariantCycle {
    private final String familyName;
    private final List<Block> blocks;
    private final Map<Block, Integer> blockToIndex;
    private final int size;
    
    /**
     * Creates a new block variant cycle.
     * 
     * @param familyName Name of the block family (e.g., "oak", "stone")
     * @param blocks List of blocks in cycle order
     */
    public BlockVariantCycle(String familyName, Block... blocks) {
        this(familyName, Arrays.asList(blocks));
    }
    
    /**
     * Creates a new block variant cycle.
     * 
     * @param familyName Name of the block family (e.g., "oak", "stone")
     * @param blocks List of blocks in cycle order
     */
    public BlockVariantCycle(String familyName, List<Block> blocks) {
        if (blocks == null || blocks.isEmpty()) {
            throw new IllegalArgumentException("Block cycle must contain at least one block");
        }
        
        this.familyName = familyName;
        this.blocks = new ArrayList<>(blocks);
        this.size = this.blocks.size();
        this.blockToIndex = new HashMap<>(size);
        
        // Build index map for O(1) lookup
        for (int i = 0; i < size; i++) {
            Block block = this.blocks.get(i);
            if (block == null) {
                throw new IllegalArgumentException("Block cycle cannot contain null blocks");
            }
            if (blockToIndex.containsKey(block)) {
                throw new IllegalArgumentException("Block cycle cannot contain duplicate blocks: " + block);
            }
            blockToIndex.put(block, i);
        }
    }
    
    /**
     * Gets the next block in the cycle.
     * 
     * @param current The current block
     * @return The next block in the cycle, or null if current is not in cycle
     */
    @Nullable
    public Block getNext(Block current) {
        Integer index = blockToIndex.get(current);
        if (index == null) {
            return null;
        }
        
        int nextIndex = (index + 1) % size;
        return blocks.get(nextIndex);
    }
    
    /**
     * Gets the previous block in the cycle.
     * 
     * @param current The current block
     * @return The previous block in the cycle, or null if current is not in cycle
     */
    @Nullable
    public Block getPrevious(Block current) {
        Integer index = blockToIndex.get(current);
        if (index == null) {
            return null;
        }
        
        int prevIndex = (index - 1 + size) % size;
        return blocks.get(prevIndex);
    }
    
    /**
     * Checks if a block is part of this cycle.
     * 
     * @param block The block to check
     * @return true if the block is in this cycle
     */
    public boolean contains(Block block) {
        return blockToIndex.containsKey(block);
    }
    
    /**
     * Gets the family name of this cycle.
     * 
     * @return The family name
     */
    public String getFamilyName() {
        return familyName;
    }
    
    /**
     * Gets all blocks in this cycle.
     * 
     * @return Unmodifiable list of blocks in cycle order
     */
    public List<Block> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }
    
    /**
     * Gets the size of this cycle.
     * 
     * @return Number of blocks in the cycle
     */
    public int size() {
        return size;
    }
    
    @Override
    public String toString() {
        return "BlockVariantCycle{" +
                "family='" + familyName + '\'' +
                ", size=" + size +
                '}';
    }
    
    /**
     * Builder for creating BlockVariantCycle with a fluent API.
     */
    public static class Builder {
        private final String familyName;
        private final List<Block> blocks = new ArrayList<>();
        
        public Builder(String familyName) {
            this.familyName = familyName;
        }
        
        public Builder add(Block block) {
            if (block != null) {
                blocks.add(block);
            }
            return this;
        }
        
        public Builder addAll(Block... blocks) {
            for (Block block : blocks) {
                add(block);
            }
            return this;
        }
        
        public Builder addAll(Collection<Block> blocks) {
            for (Block block : blocks) {
                add(block);
            }
            return this;
        }
        
        public BlockVariantCycle build() {
            return new BlockVariantCycle(familyName, blocks);
        }
    }
}
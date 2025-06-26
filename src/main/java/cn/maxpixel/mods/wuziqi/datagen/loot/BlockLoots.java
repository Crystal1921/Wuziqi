package cn.maxpixel.mods.wuziqi.datagen.loot;

import cn.maxpixel.mods.wuziqi.registry.BlockRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BlockLoots extends BlockLootSubProvider {
    private final Set<Block> skipBlocks = new HashSet<>();

    public BlockLoots(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void add(Block pBlock, LootTable.Builder pLootTableBuilder) {
        super.add(pBlock, pLootTableBuilder);
        skipBlocks.add(pBlock);
    }

    protected void skip(Block... blocks) {
        Collections.addAll(skipBlocks, blocks);
    }

    protected void skip(Collection<Block> blocks) {
        skipBlocks.addAll(blocks);
    }

    protected void dropSelfWithContents(Set<Block> blocks) {
        for (Block block : blocks) {
            if (skipBlocks.contains(block)) {
                continue;
            }
            add(block, createSingleItemTable(block));
        }
    }

    @Override
    protected void generate() {
        dropSelf(BlockRegistry.OAK_BOARD.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return skipBlocks;
    }
}

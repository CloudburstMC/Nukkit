package cn.nukkit.level.format.anvil.util;

import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockStone;
import cn.nukkit.blockstate.BlockState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.nukkit.blockstate.BlockState.AIR;
import static org.junit.jupiter.api.Assertions.*;

class BlockStorageTest {
    private static final BlockState DIRT = BlockState.of(BlockID.DIRT);
    private static final BlockState STONE = BlockState.of(BlockID.STONE);
    private static final BlockState GRANITE = BlockState.of(BlockID.STONE, BlockStone.GRANITE);
    
    BlockStorage blockStorage;
    int x;
    int y;
    int z;
    
    @BeforeEach
    void setUp() {
        blockStorage = new BlockStorage();
        x = 3;
        y = 4;
        z = 5;
    }

    @Deprecated
    @Test
    void blockData() {
        int data = 6;
        blockStorage.setBlockData(x, y, z, data);
        assertEquals(data, blockStorage.getBlockData(x, y, z));
        assertEquals(BlockState.of(0, data), blockStorage.getBlockState(x, y, z));

        blockStorage.setBlockData(x, y, z, 0);
        assertEquals(0, blockStorage.getBlockData(x, y, z));
        assertEquals(AIR, blockStorage.getBlockState(x, y, z));
    }

    @Test
    void blockId() {
        int id = BlockID.PRISMARINE;
        blockStorage.setBlockId(x, y, z, id);
        assertEquals(id, blockStorage.getBlockId(x, y, z));
        assertEquals(BlockState.of(id), blockStorage.getBlockState(x, y, z));

        blockStorage.setBlockId(x, y, z, BlockID.AIR);
        assertEquals(BlockID.AIR, blockStorage.getBlockId(x, y, z));
        assertEquals(AIR, blockStorage.getBlockState(x, y, z));
    }

    @Deprecated
    @Test
    void block() {
        int id = GRANITE.getBlockId();
        int data = GRANITE.getLegacyDamage();
        
        blockStorage.setBlock(x, y, z, id, data);
        assertEquals(BlockState.of(id, data), blockStorage.getBlockState(x, y, z));

        blockStorage.setBlock(x, y, z, BlockID.AIR, 0);
        assertEquals(AIR, blockStorage.getBlockState(x, y, z));
    }

    @Deprecated
    @Test
    void fullBlock() {
        int fullBlock = GRANITE.getFullId();
        blockStorage.setFullBlock(x, y, z, fullBlock);
        assertEquals(fullBlock, blockStorage.getFullBlock(x, y, z));
        assertEquals(GRANITE, blockStorage.getBlockState(x, y, z));
        
        blockStorage.setFullBlock(x, y, z, 0);
        assertEquals(0, blockStorage.getFullBlock(x, y, z));
        assertEquals(AIR, blockStorage.getBlockState(x, y, z));
    }

    @Deprecated
    @Test
    void getAndSetBlock() {
        BlockState oldBlock = blockStorage.getAndSetBlock(x, y, z, GRANITE.getBlockId(), GRANITE.getLegacyDamage());
        assertEquals(AIR, oldBlock);
        assertEquals(GRANITE, blockStorage.getBlockState(x, y, z));
        
        oldBlock = blockStorage.getAndSetBlock(x, y, z, DIRT.getBlockId(), DIRT.getLegacyDamage());
        assertEquals(GRANITE, oldBlock);
        assertEquals(DIRT, blockStorage.getBlockState(x, y, z));

        oldBlock = blockStorage.getAndSetBlock(x, y, z, AIR.getBlockId(), AIR.getLegacyDamage());
        assertEquals(DIRT, oldBlock);
        assertEquals(AIR, blockStorage.getBlockState(x, y, z));
    }

    @Test
    void getAndSetBlockState() {
        BlockState oldBlock = blockStorage.getAndSetBlockState(x, y, z, GRANITE);
        assertEquals(AIR, oldBlock);
        assertEquals(GRANITE, blockStorage.getBlockState(x, y, z));

        oldBlock = blockStorage.getAndSetBlockState(x, y, z, DIRT);
        assertEquals(GRANITE, oldBlock);
        assertEquals(DIRT, blockStorage.getBlockState(x, y, z));

        oldBlock = blockStorage.getAndSetBlockState(x, y, z, AIR);
        assertEquals(DIRT, oldBlock);
        assertEquals(AIR, blockStorage.getBlockState(x, y, z));
    }

    @Test
    void blockState() {
        assertEquals(AIR, blockStorage.getBlockState(x, y, z));
        
        blockStorage.setBlockState(x, y, z, GRANITE);
        assertEquals(GRANITE, blockStorage.getBlockState(x, y, z));

        blockStorage.setBlockState(x, y, z, STONE);
        assertEquals(STONE, blockStorage.getBlockState(x, y, z));

        blockStorage.setBlockState(x, y, z, GRANITE);
        assertEquals(GRANITE, blockStorage.getBlockState(x, y, z));
    }

    @Deprecated
    @Test
    void getAndSetFullBlock() {
        int oldBlock = blockStorage.getAndSetFullBlock(x, y, z, GRANITE.getFullId());
        assertEquals(AIR.getFullId(), oldBlock);
        assertEquals(GRANITE, blockStorage.getBlockState(x, y, z));

        oldBlock = blockStorage.getAndSetFullBlock(x, y, z, DIRT.getFullId());
        assertEquals(GRANITE.getFullId(), oldBlock);
        assertEquals(DIRT, blockStorage.getBlockState(x, y, z));

        oldBlock = blockStorage.getAndSetFullBlock(x, y, z, AIR.getFullId());
        assertEquals(DIRT.getFullId(), oldBlock);
        assertEquals(AIR, blockStorage.getBlockState(x, y, z));
    }

    @Test
    void copy() {
        BlockStorage copy = blockStorage.copy();
        
        copy.setBlockState(x, y, z, GRANITE);
        assertEquals(GRANITE, copy.getBlockState(x, y, z));
        assertEquals(AIR, blockStorage.getBlockState(x, y, z));
    }

    @Test
    void hasBlockIds() {
        assertFalse(blockStorage.hasBlockIds());
        assertFalse(blockStorage.hasBlockIdExtras());
        assertFalse(blockStorage.hasBlockDataExtras());
        assertFalse(blockStorage.hasBlockDataBig());
        assertFalse(blockStorage.hasBlockDataHuge());
        
        blockStorage.setBlockState(x, y, z, STONE);
        assertTrue(blockStorage.hasBlockIds());
        assertFalse(blockStorage.hasBlockIdExtras());
        assertFalse(blockStorage.hasBlockDataExtras());
        assertFalse(blockStorage.hasBlockDataBig());
        assertFalse(blockStorage.hasBlockDataHuge());

        blockStorage.setBlockState(x, y, z, AIR);
        blockStorage.recheckBlocks();
        assertFalse(blockStorage.hasBlockIds());
        assertFalse(blockStorage.hasBlockIdExtras());
        assertFalse(blockStorage.hasBlockDataExtras());
        assertFalse(blockStorage.hasBlockDataBig());
        assertFalse(blockStorage.hasBlockDataHuge());
    }

    @Test
    void hasBlockIdExtras() {
        assertFalse(blockStorage.hasBlockIds());
        assertFalse(blockStorage.hasBlockIdExtras());
        assertFalse(blockStorage.hasBlockDataExtras());
        assertFalse(blockStorage.hasBlockDataBig());
        assertFalse(blockStorage.hasBlockDataHuge());

        blockStorage.setBlockState(x, y, z, BlockState.of(500));
        assertTrue(blockStorage.hasBlockIds());
        assertTrue(blockStorage.hasBlockIdExtras());
        assertFalse(blockStorage.hasBlockDataExtras());
        assertFalse(blockStorage.hasBlockDataBig());
        assertFalse(blockStorage.hasBlockDataHuge());

        blockStorage.setBlockState(x, y, z, AIR);
        blockStorage.recheckBlocks();
        assertFalse(blockStorage.hasBlockIds());
        assertFalse(blockStorage.hasBlockIdExtras());
        assertFalse(blockStorage.hasBlockDataExtras());
        assertFalse(blockStorage.hasBlockDataBig());
        assertFalse(blockStorage.hasBlockDataHuge());
    }

    @Test
    void hasBlockDataExtras() {
        assertFalse(blockStorage.hasBlockIds());
        assertFalse(blockStorage.hasBlockIdExtras());
        assertFalse(blockStorage.hasBlockDataExtras());
        assertFalse(blockStorage.hasBlockDataBig());
        assertFalse(blockStorage.hasBlockDataHuge());

        blockStorage.setBlockState(x, y, z, BlockState.of(0, 0x0F));
        assertTrue(blockStorage.hasBlockIds());
        assertFalse(blockStorage.hasBlockIdExtras());
        assertFalse(blockStorage.hasBlockDataExtras());
        assertFalse(blockStorage.hasBlockDataBig());
        assertFalse(blockStorage.hasBlockDataHuge());

        blockStorage.setBlockState(x, y, z, AIR);
        blockStorage.recheckBlocks();
        assertFalse(blockStorage.hasBlockIds());
        assertFalse(blockStorage.hasBlockIdExtras());
        assertFalse(blockStorage.hasBlockDataExtras());
        assertFalse(blockStorage.hasBlockDataBig());
        assertFalse(blockStorage.hasBlockDataHuge());

        blockStorage.setBlockState(x, y, z, BlockState.of(0, 0x1F));
        assertTrue(blockStorage.hasBlockIds());
        assertFalse(blockStorage.hasBlockIdExtras());
        assertTrue(blockStorage.hasBlockDataExtras());
        assertFalse(blockStorage.hasBlockDataBig());
        assertFalse(blockStorage.hasBlockDataHuge());

        blockStorage.setBlockState(x, y, z, AIR);
        blockStorage.recheckBlocks();
        assertFalse(blockStorage.hasBlockIds());
        assertFalse(blockStorage.hasBlockIdExtras());
        assertFalse(blockStorage.hasBlockDataExtras());
        assertFalse(blockStorage.hasBlockDataBig());
        assertFalse(blockStorage.hasBlockDataHuge());
    }

    @Test
    void hasBlockDataBig() {
        assertFalse(blockStorage.hasBlockIds());
        assertFalse(blockStorage.hasBlockIdExtras());
        assertFalse(blockStorage.hasBlockDataExtras());
        assertFalse(blockStorage.hasBlockDataBig());
        assertFalse(blockStorage.hasBlockDataHuge());

        blockStorage.setBlockState(x, y, z, BlockState.of(0, 0x1FF));
        assertTrue(blockStorage.hasBlockIds());
        assertFalse(blockStorage.hasBlockIdExtras());
        assertTrue(blockStorage.hasBlockDataExtras());
        assertTrue(blockStorage.hasBlockDataBig());
        assertFalse(blockStorage.hasBlockDataHuge());

        blockStorage.setBlockState(x, y, z, AIR);
        blockStorage.recheckBlocks();
        assertFalse(blockStorage.hasBlockIds());
        assertFalse(blockStorage.hasBlockIdExtras());
        assertFalse(blockStorage.hasBlockDataExtras());
        assertFalse(blockStorage.hasBlockDataBig());
        assertFalse(blockStorage.hasBlockDataHuge());
    }

    @Test
    void hasBlockDataHuge() {
        assertFalse(blockStorage.hasBlockIds());
        assertFalse(blockStorage.hasBlockIdExtras());
        assertFalse(blockStorage.hasBlockDataExtras());
        assertFalse(blockStorage.hasBlockDataBig());
        assertFalse(blockStorage.hasBlockDataHuge());

        blockStorage.setBlockState(x, y, z, BlockState.of(0, 0x1FFFF));
        assertTrue(blockStorage.hasBlockIds());
        assertFalse(blockStorage.hasBlockIdExtras());
        assertTrue(blockStorage.hasBlockDataExtras());
        assertTrue(blockStorage.hasBlockDataBig());
        assertTrue(blockStorage.hasBlockDataHuge());

        blockStorage.setBlockState(x, y, z, AIR);
        blockStorage.recheckBlocks();
        assertFalse(blockStorage.hasBlockIds());
        assertFalse(blockStorage.hasBlockIdExtras());
        assertFalse(blockStorage.hasBlockDataExtras());
        assertFalse(blockStorage.hasBlockDataBig());
        assertFalse(blockStorage.hasBlockDataHuge());
    }

    @Test
    void iterateStates() {
        AtomicInteger hits = new AtomicInteger();
        AtomicBoolean found = new AtomicBoolean(false);
        
        blockStorage.setBlockState(x, y, z, GRANITE);
        blockStorage.iterateStates(((x1, y1, z1, data) -> {
            hits.getAndIncrement();
            if (x1 == x && y1 == y && z1 == z) {
                assertEquals(GRANITE, data);
                assertFalse(found.getAndSet(true));
            } else {
                assertEquals(AIR, data);
            }
        }));
        
        assertTrue(found.get());
        assertEquals(BlockStorage.SECTION_SIZE, hits.get());
    }
}

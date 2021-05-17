/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.level.format.anvil;

import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.anvil.util.BlockStorage;
import cn.nukkit.level.format.anvil.util.ImmutableBlockStorage;
import cn.nukkit.level.format.updater.ChunkUpdater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author joserobjr
 * @since 2020-10-06
 */
@ExtendWith(PowerNukkitExtension.class)
class MultiLayerStorageTest {

    MultiLayerStorage storage;

    @BeforeEach
    void setUp() {
        storage = new MultiLayerStorage();
    }

    @Test
    void hasBlocks() {
        assertFalse(storage.hasBlocks());
        BlockStorage blockStorage = getBlockStorage(1);
        assertFalse(storage.hasBlocks());
        blockStorage.setBlockId(0,1,2, BlockID.FIRE);
        assertTrue(storage.hasBlocks());
        blockStorage.setBlockId(0,1,2, BlockID.AIR);
        assertTrue(storage.hasBlocks());
        blockStorage.recheckBlocks();
        assertFalse(storage.hasBlocks());
        blockStorage.setBlockId(0,1,2, BlockID.ACACIA_WALL_SIGN);
        assertTrue(storage.hasBlocks());
    }
    
    private void unexpectedSetStorage(LayerStorage unexpected) {
        fail("Unexpected call setting to " + unexpected);
    }
    
    private int getContentVersion() {
        return ChunkUpdater.getCurrentContentVersion();
    }
    
    private BlockStorage getBlockStorage(int layer) {
        return getBlockStorage(storage, layer);
    }
    
    private BlockStorage getBlockStorage(LayerStorage layerStorage, int layer) {
        return layerStorage.getOrSetStorage(this::unexpectedSetStorage, this::getContentVersion, layer);
    }

    @Test
    void testClone() {
        BlockStorage blockStorage = getBlockStorage(1);
        blockStorage.setBlockId(0,1,2, BlockID.FIRE);
        MultiLayerStorage clone = storage.clone();
        assertTrue(clone.hasBlocks());
        BlockStorage blockStorageCloned = getBlockStorage(clone, 1);
        assertNotEquals(blockStorage, blockStorageCloned);
        assertNotEquals(storage, clone);
        blockStorageCloned.setBlockId(2,2,2, BlockID.COBBLESTONE);
        assertEquals(BlockState.AIR, blockStorage.getBlockState(2,2,2));
    }

    @Test
    void getStorageOrEmpty() {
        assertEquals(ImmutableBlockStorage.EMPTY, storage.getStorageOrEmpty(1));
        BlockStorage blockStorage = getBlockStorage(1);
        blockStorage.setBlockId(1,2,3,BlockID.STRUCTURE_VOID);
        assertThat(storage.getStorageOrEmpty(1)).isNotNull().isNotInstanceOf(ImmutableBlockStorage.class);
    }

    @Test
    void getOrSetStorage() {
        BlockStorage blockStorage = getBlockStorage(1);
        assertEquals(blockStorage, getBlockStorage(1));
        blockStorage.setBlockId(1,2,3,BlockID.ACACIA_TRAPDOOR);
        assertEquals(blockStorage, getBlockStorage(1));

        assertThrows(IndexOutOfBoundsException.class, ()-> storage.getOrSetStorage(this::unexpectedSetStorage, this::getContentVersion, 2));
    }

    @Test
    void getStorageOrNull() {
        assertNull(storage.getStorageOrNull(1));
        BlockStorage blockStorage = getBlockStorage(1);
        blockStorage.setBlockId(1,2,3,BlockID.STRUCTURE_VOID);
        assertThat(storage.getStorageOrNull(1)).isNotNull().isNotInstanceOf(ImmutableBlockStorage.class);
    }

    @Test
    void delayPaletteUpdates() {
        BlockStorage blockStorage = getBlockStorage(1);
        assertFalse(blockStorage.isPaletteUpdateDelayed());
        storage.delayPaletteUpdates();
        assertTrue(blockStorage.isPaletteUpdateDelayed());
    }

    @Test
    void size() {
        assertEquals(0, storage.size());
        getBlockStorage(0);
        assertEquals(1, storage.size());
        getBlockStorage(1);
        assertEquals(2, storage.size());
    }

    @Test
    void compress() {
        AtomicBoolean attemptedToSet = new AtomicBoolean();
        Consumer<LayerStorage> toEmpty = reduced -> {
            assertThat(reduced).isEqualTo(LayerStorage.EMPTY);
            attemptedToSet.set(true);
        };
        storage.compress(toEmpty);
        assertTrue(attemptedToSet.get());
        
        attemptedToSet.set(false);
        getBlockStorage(0);
        storage.compress(toEmpty);
        assertTrue(attemptedToSet.get());

        attemptedToSet.set(false);
        getBlockStorage(1);
        storage.compress(toEmpty);
        assertTrue(attemptedToSet.get());
        
        getBlockStorage(0).setBlockId(1,2,3, BlockID.ACACIA_TRAPDOOR);
        Consumer<LayerStorage> toSingle = reduced -> {
            assertThat(reduced).isInstanceOf(SingleLayerStorage.class);
            attemptedToSet.set(true);
        };
        attemptedToSet.set(false);
        storage.compress(toSingle);
        assertTrue(attemptedToSet.get());
        
        getBlockStorage(1);
        attemptedToSet.set(false);
        storage.compress(toSingle);
        assertTrue(attemptedToSet.get());

        getBlockStorage(1).setBlockId(1,2,3, BlockID.STILL_WATER);
        storage.compress(this::unexpectedSetStorage);
    }
}

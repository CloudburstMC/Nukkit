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
class SingleLayerStorageTest {

    SingleLayerStorage storage;

    @BeforeEach
    void setUp() {
        storage = new SingleLayerStorage();
    }

    @Test
    void hasBlocks() {
        assertFalse(storage.hasBlocks());
        BlockStorage blockStorage = getBlockStorage();
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
    
    private BlockStorage getBlockStorage() {
        return getBlockStorage(storage);
    }
    
    private BlockStorage getBlockStorage(LayerStorage layerStorage) {
        return layerStorage.getOrSetStorage(this::unexpectedSetStorage, this::getContentVersion, 0);
    }

    @Test
    void testClone() {
        BlockStorage blockStorage = getBlockStorage();
        blockStorage.setBlockId(0,1,2, BlockID.FIRE);
        SingleLayerStorage clone = storage.clone();
        assertTrue(clone.hasBlocks());
        BlockStorage blockStorageCloned = getBlockStorage(clone);
        assertNotEquals(blockStorage, blockStorageCloned);
        assertNotEquals(storage, clone);
        blockStorageCloned.setBlockId(2,2,2, BlockID.COBBLESTONE);
        assertEquals(BlockState.AIR, blockStorage.getBlockState(2,2,2));
    }

    @Test
    void getStorageOrEmpty() {
        assertEquals(ImmutableBlockStorage.EMPTY, storage.getStorageOrEmpty(0));
        BlockStorage blockStorage = getBlockStorage();
        blockStorage.setBlockId(1,2,3,BlockID.STRUCTURE_VOID);
        assertThat(storage.getStorageOrEmpty(0)).isNotNull().isNotInstanceOf(ImmutableBlockStorage.class);
    }

    @Test
    void getOrSetStorage() {
        BlockStorage blockStorage = getBlockStorage();
        assertEquals(blockStorage, getBlockStorage());
        blockStorage.setBlockId(1,2,3,BlockID.ACACIA_TRAPDOOR);
        assertEquals(blockStorage, getBlockStorage());

        AtomicBoolean attemptedToSet = new AtomicBoolean();
        storage.getOrSetStorage(expanded -> {
            assertThat(expanded).isInstanceOf(MultiLayerStorage.class);
            assertThat(expanded.getStorageOrNull(1)).isNotNull().isNotInstanceOf(ImmutableBlockStorage.class);
            attemptedToSet.set(true);
        }, this::getContentVersion, 1);
        
        assertTrue(attemptedToSet.get());
    }

    @Test
    void getStorageOrNull() {
        assertNull(storage.getStorageOrNull(0));
        BlockStorage blockStorage = getBlockStorage();
        blockStorage.setBlockId(1,2,3,BlockID.STRUCTURE_VOID);
        assertThat(storage.getStorageOrNull(0)).isNotNull().isNotInstanceOf(ImmutableBlockStorage.class);
    }

    @Test
    void delayPaletteUpdates() {
        BlockStorage blockStorage = getBlockStorage();
        assertFalse(blockStorage.isPaletteUpdateDelayed());
        storage.delayPaletteUpdates();
        assertTrue(blockStorage.isPaletteUpdateDelayed());
    }

    @Test
    void size() {
        assertEquals(0, storage.size());
        getBlockStorage();
        assertEquals(1, storage.size());
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
        getBlockStorage();
        storage.compress(toEmpty);
        assertTrue(attemptedToSet.get());
        
        getBlockStorage().setBlockId(1,2,3, BlockID.ACACIA_TRAPDOOR);
        storage.compress(this::unexpectedSetStorage);
    }
}

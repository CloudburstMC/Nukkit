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

package cn.nukkit.level.format.anvil.util;

import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author joserobjr
 * @since 2020-10-02
 */
@ExtendWith(PowerNukkitExtension.class)
class ImmutableBlockStorageTest {
    ImmutableBlockStorage storage;
    private final BlockState stone = BlockState.of(BlockID.STONE);

    @BeforeEach
    void setUp() {
        storage = new BlockStorage().immutableCopy();
    }

    @Test
    void setBlockId() {
        assertThrows(UnsupportedOperationException.class, ()-> storage.setBlockId(0, 0, 0, 1));
    }

    @Test
    void setBlockData() {
        assertThrows(UnsupportedOperationException.class, ()-> storage.setBlockData(0, 0, 0, 1));
    }

    @Test
    void setBlock() {
        assertThrows(UnsupportedOperationException.class, ()-> storage.setBlock(0, 0, 0, 1, 2));
    }

    @Test
    void getAndSetBlock() {
        assertThrows(UnsupportedOperationException.class, ()-> storage.getAndSetBlock(0, 0, 0, 1, 2));
    }

    @Test
    void getAndSetBlockState() {
        assertThrows(UnsupportedOperationException.class, ()-> storage.getAndSetBlockState(0, 0, 0, stone));
    }

    @Test
    void setBlockState() {
        assertThrows(UnsupportedOperationException.class, ()-> storage.setBlockState(0, 0, 0, stone));
    }

    @Test
    void getAndSetFullBlock() {
        assertThrows(UnsupportedOperationException.class, ()-> storage.getAndSetFullBlock(0, 0, 0, stone.getFullId()));
    }

    @Test
    void testSetBlockState() {
        assertThrows(UnsupportedOperationException.class, ()-> storage.setBlockState(0, stone));
    }

    @Test
    void delayPaletteUpdates() {
        storage.delayPaletteUpdates();
        assertFalse(storage.isPaletteUpdateDelayed());
    }

    @Test
    void recheckBlocks() {
        storage.recheckBlocks();
        assertFalse(storage.hasBlockIds());
    }

    @Test
    void copy() {
        assertFalse(storage.copy() instanceof ImmutableBlockStorage);
    }

    @Test
    void immutableCopy() {
        assertTrue(storage == storage.immutableCopy());
    }
}

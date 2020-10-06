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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author joserobjr
 * @since 2020-10-05
 */
@ExtendWith(PowerNukkitExtension.class)
class ChunkTest {
     final int x = 5, y = 6, z = 7;
    
    Chunk chunk;

    @BeforeEach
    void setUp() {
        chunk = new Chunk(Anvil.class);
    }

    @Test
    void emptyMiddleSection() {
        chunk.setBlockState(x, y, z, BlockState.of(BlockID.STONE));
        chunk.setBlockState(x, y+100, z, BlockState.of(BlockID.DIRT));
        Consumer<Chunk> checker = chunk -> {
            assertFalse(chunk.getSection(0).isEmpty());
            assertTrue(chunk.getSection(1).isEmpty());
            assertFalse(chunk.getSection((y + 100) >> 4).isEmpty());
        };
        
        checker.accept(chunk);
        
        byte[] persisted = chunk.toBinary();
        Chunk reloaded = Chunk.fromBinary(persisted);
        checker.accept(reloaded);
        
        reloaded.setBlockState(x, y, z, BlockState.AIR);
        checker.accept(reloaded);
        
        persisted = reloaded.toBinary();
        reloaded = Chunk.fromBinary(persisted);
        assertTrue(reloaded.getSection(0).isEmpty());
        assertTrue(reloaded.getSection(1).isEmpty());
        assertFalse(reloaded.getSection((y + 100) >> 4).isEmpty());
    }
}

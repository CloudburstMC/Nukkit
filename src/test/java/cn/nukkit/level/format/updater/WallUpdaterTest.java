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

package cn.nukkit.level.format.updater;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockWall;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.anvil.ChunkSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author joserobjr
 * @since 2020-09-20
 */
@ExtendWith(PowerNukkitExtension.class)
class WallUpdaterTest implements BlockID {
    int x = 3, y = 4, z = 5;
    
    @MockLevel
    Level level;

    ChunkSection section;
    
    WallUpdater updater;

    @Test
    void update() {
        BlockState state = BlockState.of(COBBLE_WALL, 63);
        section.setBlockState(x, y, z, state);
        assertTrue(updater.update(0, 0, 0, x, y, z, state));
        BlockWall wall = new BlockWall();
        wall.setWallPost(true);
        Block actual = section.getBlockState(x, y, z).getBlock();
        assertEquals(wall.getCurrentState(), section.getBlockState(x, y, z));
        assertEquals(wall.getCurrentState(), actual.getCurrentState());
    }

    @BeforeEach
    void setUp() {
        section = new ChunkSection(0);
        section.setContentVersion(0);
        section.delayPaletteUpdates();
        
        updater = new WallUpdater(level, section);
    }
}

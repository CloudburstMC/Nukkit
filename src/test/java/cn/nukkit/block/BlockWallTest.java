/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2021  José Roberto de Araújo Júnior
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

package cn.nukkit.block;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author joserobjr
 * @since 2021-03-24
 */
@ExtendWith(PowerNukkitExtension.class)
class BlockWallTest {
    private static final int X = 1, Y = 2, Z = 3;
    
    @MockLevel
    Level level;
    
    BlockWall wall;
    
    Item diamondPickaxe;

    @BeforeEach
    void setUp() {
        diamondPickaxe = Item.get(ItemID.DIAMOND_PICKAXE);
        wall = (BlockWall) BlockState.of(BlockID.STONE_WALL).getBlock(level, X, Y, Z);
        level.setBlock(wall, wall, false, false);
    }

    @ParameterizedTest
    @EnumSource(BlockWall.WallType.class)
    void getDrops(BlockWall.WallType wallType) {
        wall.setWallType(wallType);
        Item[] drops = wall.getDrops(diamondPickaxe);
        assertEquals(1, drops.length);
        assertEquals(Item.getBlock(BlockID.STONE_WALL, 
                BlockState.of(BlockID.STONE_WALL)
                        .withProperty(BlockWall.WALL_BLOCK_TYPE, wallType).getExactIntStorage()), 
                drops[0]);
    }
}

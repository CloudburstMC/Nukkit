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

package cn.nukkit.entity.item;

import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author joserobjr
 * @since 2021-06-29
 */
@ExtendWith(PowerNukkitExtension.class)
class EntityItemTest {
    @MockLevel
    Level level;
    EntityItem entityItem;

    @BeforeEach
    void setUp() {
        level.setBlockStateAt(0, 63, 0, BlockState.of(BlockID.STONE));
        entityItem = new EntityItem(level.getChunk(0, 0), Entity.getDefaultNBT(new Vector3(0, 64, 0)));
    }

    @Test
    void getNameWithCustomName() {
        entityItem.setNameTag(" ");
        assertEquals(" ", entityItem.getName());
    }

    @Test
    void getNameWithNullItem() {
        entityItem.item = null;
        assertEquals("Item", entityItem.getName());
    }

    @Test
    void getNameWithItemWithCustomName() {
        entityItem.item = Item.get(ItemID.GOLD_SWORD);
        entityItem.item.setCustomName("God's Sword");
        assertEquals("1x God's Sword", entityItem.getName());
    }

    @Test
    void getNameWithItem() {
        entityItem.item = Item.get(ItemID.GOLD_SWORD);
        assertEquals("1x Gold Sword", entityItem.getName());
    }
}

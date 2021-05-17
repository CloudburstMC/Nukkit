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

package cn.nukkit.blockproperty.value;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlower;
import cn.nukkit.block.BlockID;
import cn.nukkit.utils.DyeColor;
import lombok.RequiredArgsConstructor;

/**
 * @author joserobjr
 * @since 2020-10-10
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@RequiredArgsConstructor
public enum SmallFlowerType {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    POPPY("Poppy", DyeColor.RED, BlockID.RED_FLOWER),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    ORCHID("Blue Orchid", DyeColor.LIGHT_BLUE, BlockID.RED_FLOWER),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    ALLIUM("Allium", DyeColor.MAGENTA, BlockID.RED_FLOWER),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    HOUSTONIA("Azure Bluet", DyeColor.LIGHT_GRAY, BlockID.RED_FLOWER),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    TULIP_RED("Red Tulip", DyeColor.RED, BlockID.RED_FLOWER),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    TULIP_ORANGE("Orange Tulip", DyeColor.ORANGE, BlockID.RED_FLOWER),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    TULIP_WHITE("White Tulip", DyeColor.LIGHT_GRAY, BlockID.RED_FLOWER),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    TULIP_PINK("Pink Tulip", DyeColor.PINK, BlockID.RED_FLOWER),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    OXEYE("Oxeye Daisy", DyeColor.LIGHT_GRAY, BlockID.RED_FLOWER),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    CORNFLOWER("Cornflower", DyeColor.BLUE, BlockID.RED_FLOWER),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    LILY_OF_THE_VALLEY("Lily of the Valley", DyeColor.WHITE, BlockID.RED_FLOWER),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    DANDELION("Dandelion", DyeColor.YELLOW, BlockID.DANDELION),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    WITHER_ROSE("Wither Rose", DyeColor.BLACK, BlockID.WITHER_ROSE);

    private final String englishName;
    private final DyeColor dyeColor;
    private final int blockId;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public DyeColor getDyeColor() {
        return dyeColor;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public String getEnglishName() {
        return englishName;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getBlockId() {
        return blockId;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockFlower getBlock() {
        BlockFlower flower = (BlockFlower) Block.get(getBlockId());
        flower.setFlowerType(this);
        return flower;
    }
}

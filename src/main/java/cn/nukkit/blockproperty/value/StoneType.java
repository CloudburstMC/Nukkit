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
import cn.nukkit.utils.BlockColor;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;

import static cn.nukkit.utils.BlockColor.*;

/**
 * @author joserobjr
 * @since 2020-10-04
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@AllArgsConstructor
public enum StoneType {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    STONE("Stone", STONE_BLOCK_COLOR),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    GRANITE("Granite", DIRT_BLOCK_COLOR),
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    GRANITE_SMOOTH("Polished Granite", DIRT_BLOCK_COLOR),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    DIORITE("Diorite", QUARTZ_BLOCK_COLOR),
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    DIORITE_SMOOTH("Polished Diorite", QUARTZ_BLOCK_COLOR),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    ANDESITE("Andesite", STONE_BLOCK_COLOR),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    ANDESITE_SMOOTH("Polished Andesite", STONE_BLOCK_COLOR);
    
    private final String englishName;
    private final BlockColor color;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public String getEnglishName() {
        return englishName;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public BlockColor getColor() {
        return color;
    }
}

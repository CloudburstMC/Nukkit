/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.block.entity;

import cn.nukkit.api.block.entity.BedBlockEntity;
import cn.nukkit.api.util.data.DyeColor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NukkitBedBlockEntity implements BedBlockEntity {
    private DyeColor dyeColor;

    @Override
    public DyeColor getDyeColor() {
        return dyeColor;
    }

    @Override
    public void setDyeColor(DyeColor dyeColor) {
        this.dyeColor = dyeColor;
    }
}

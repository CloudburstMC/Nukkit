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

package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.Colorable;
import cn.nukkit.api.util.data.DyeColor;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

public class ColorableComponent implements Colorable {
    private DyeColor color;

    public ColorableComponent(DyeColor color) {
        this.color = color;
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public void setColor(@Nonnull DyeColor color) {
        this.color = Preconditions.checkNotNull(color, "color");
    }
}

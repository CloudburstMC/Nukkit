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

import cn.nukkit.api.entity.component.Explode;

public class ExplodableComponent implements Explode {
    private final int fuse;
    private int radius;
    private boolean incendiary;

    public ExplodableComponent(int fuse, int radius, boolean incendiary) {
        this.fuse = fuse;
        this.radius = radius;
        this.incendiary = incendiary;
    }

    public int getFuse() {
        return fuse;
    }

    @Override
    public boolean isPrimed() {
        return false; //TODO
    }

    @Override
    public int getRadius() {
        return radius;
    }

    @Override
    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public void setIsIncendiary(boolean incendiary) {
        this.incendiary = incendiary;
    }

    @Override
    public boolean isIncendiary() {
        return incendiary;
    }
}

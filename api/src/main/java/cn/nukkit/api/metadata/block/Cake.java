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

package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;
import com.google.common.base.Preconditions;

public class Cake implements Metadata {
    public static final Cake NEW = new Cake(0);
    public static final Cake ALMOST_EATEN = new Cake(6);
    private final int level;

    private Cake(int level) {
        this.level = level;
    }

    public static Cake of(short data) {
        Preconditions.checkArgument(data >= 0 && data < 8, "data is not valid (wanted 0-7)");
        return new Cake(data);
    }

    public int getSlicesEaten() {
        return level;
    }

    public int getSlicesLeft() {
        return 7 - level;
    }
}

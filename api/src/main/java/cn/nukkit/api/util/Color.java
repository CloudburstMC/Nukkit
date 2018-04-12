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

package cn.nukkit.api.util;

import com.google.common.base.Preconditions;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Color {
    private final byte r;
    private final byte g;
    private final byte b;

    private Color(int r, int b, int g) {
        Preconditions.checkArgument(r >= 0 && r <= 255, "r is not 0 - 255");
        Preconditions.checkArgument(g >= 0 && g <= 255, "g is not 0 - 255");
        Preconditions.checkArgument(b >= 0 && b <= 255, "b is not 0 - 255");
        this.r = (byte) r;
        this.b = (byte) b;
        this.g = (byte) g;
    }

    public static Color of(int r, int g, int b) {
        return new Color(r, g, b);
    }

    public int getR() {
        return (r & 0xff);
    }

    public int getB() {
        return (b & 0xff);
    }

    public int getG() {
        return (g & 0xff);
    }
}

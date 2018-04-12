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

import cn.nukkit.api.level.Level;
import com.flowpowered.math.vector.Vector3f;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Location {
    private final Vector3f position;
    private final Rotation rotation;
    private final Level level;

    public Location(Vector3f position, Level level) {
        this(position, level, Rotation.ZERO);
    }

    public Location(Vector3f position, Level level, Rotation rotation) {
        this.position = position;
        this.rotation = rotation;
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    public Location setLevel(Level level) {
        return new Location(position, level, rotation);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Location setPosition(Vector3f position) {
        return new Location(position, level, rotation);
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Location setRotation(Rotation rotation) {
        return new Location(position, level, rotation);
    }
}

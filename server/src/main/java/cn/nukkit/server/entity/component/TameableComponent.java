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

import cn.nukkit.api.Player;
import cn.nukkit.api.entity.component.Tameable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class TameableComponent implements Tameable {
    private Player owner;
    private boolean tamed;

    @Override
    public boolean isTamed() {
        return tamed || owner != null;
    }

    @Override
    public void setTamed(boolean tamed) {
        this.tamed = tamed;
    }

    @Nonnull
    @Override
    public Optional<Player> getOwner() {
        return Optional.ofNullable(owner);
    }

    @Override
    public void setOwner(@Nullable Player owner) {
        this.owner = owner;
    }
}

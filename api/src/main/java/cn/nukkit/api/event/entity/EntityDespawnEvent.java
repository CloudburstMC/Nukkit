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

package cn.nukkit.api.event.entity;

import cn.nukkit.api.Player;
import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.entity.component.Damageable;
import cn.nukkit.api.entity.component.Projectile;
import cn.nukkit.api.entity.component.Rideable;
import cn.nukkit.api.entity.misc.DroppedItem;
import com.flowpowered.math.vector.Vector3f;

public class EntityDespawnEvent implements EntityEvent {
    private final Entity entity;

    public EntityDespawnEvent(Entity entity) {
        this.entity = entity;
    }

    public Vector3f getPosition() {
        return this.entity.getPosition();
    }

    public boolean isCreature() {
        return this.entity.get(Damageable.class).isPresent();
    }

    public boolean isHuman() {
        return this.entity instanceof Player;
    }

    public boolean isProjectile() {
        return this.entity.get(Projectile.class).isPresent();
    }

    public boolean isVehicle() {
        return this.entity.get(Rideable.class).isPresent();
    }

    public boolean isItem() {
        return this.entity instanceof DroppedItem;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }
}

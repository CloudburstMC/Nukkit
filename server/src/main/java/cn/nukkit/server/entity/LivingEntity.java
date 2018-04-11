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

package cn.nukkit.server.entity;

import cn.nukkit.api.entity.component.Damageable;
import cn.nukkit.api.entity.component.Equippable;
import cn.nukkit.api.entity.component.Physics;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.component.DamageableComponent;
import cn.nukkit.server.entity.component.EquippableComponent;
import cn.nukkit.server.entity.component.PhysicsComponent;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public abstract class LivingEntity extends BaseEntity {

    protected LivingEntity(EntityType data, Vector3f position, NukkitLevel level, NukkitServer server, int maximumHealth) {
        super(data, position, level, server);

        this.registerComponent(Damageable.class, new DamageableComponent(this, maximumHealth));
        this.registerComponent(Equippable.class, new EquippableComponent());
        this.registerComponent(Physics.class, new PhysicsComponent(0.08f, 0.02f));
    }
}

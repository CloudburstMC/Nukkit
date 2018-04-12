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

package cn.nukkit.server.entity.misc;

import cn.nukkit.api.entity.component.ContainedExperience;
import cn.nukkit.api.entity.component.Physics;
import cn.nukkit.api.entity.component.PickupDelay;
import cn.nukkit.api.entity.misc.ExperienceOrb;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.component.ContainedExperienceComponent;
import cn.nukkit.server.entity.component.PhysicsComponent;
import cn.nukkit.server.entity.component.PickupDelayComponent;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;


public class ExperienceOrbEntity extends BaseEntity implements ExperienceOrb {

    public ExperienceOrbEntity(Vector3f position, NukkitLevel level, NukkitServer server, int experience) {
        super(EntityType.EXPERIENCE_ORB, position, level, server);

        registerComponent(ContainedExperience.class, new ContainedExperienceComponent(experience));
        registerComponent(PickupDelay.class, new PickupDelayComponent(20));
        registerComponent(Physics.class, new PhysicsComponent(0.04f, 0.02f));
    }
}
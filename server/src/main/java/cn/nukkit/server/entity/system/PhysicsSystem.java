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

package cn.nukkit.server.entity.system;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.entity.component.Physics;
import cn.nukkit.api.entity.system.System;
import cn.nukkit.api.entity.system.SystemRunner;
import com.flowpowered.math.vector.Vector3f;

public class PhysicsSystem implements SystemRunner {
    public static final System SYSTEM = System.builder()
            .expectComponent(Physics.class)
            .runner(new PhysicsSystem())
            .build();

    private PhysicsSystem() {

    }

    @Override
    public void run(Entity entity) {
        Physics physics = entity.ensureAndGet(Physics.class);
        if (entity.getMotion().equals(Vector3f.ZERO)) {
            if (entity.isOnGround()) {
                entity.setMotion(entity.getMotion().add(0, physics.getGravity(), 0));
            } else {
                entity.setMotion(entity.getMotion().sub(0, physics.getGravity(), 0));
                entity.setPositionFromSystem(entity.getPosition().add(entity.getMotion()));
            }
        }
        if (entity.getMotion().lengthSquared() > 0) {
            boolean onGroundPreviously = entity.isOnGround();
            entity.setPositionFromSystem(entity.getPosition().add(entity.getMotion()));
            boolean onGroundNow = entity.isOnGround();

            if (!onGroundPreviously && onGroundNow) {
                entity.setPositionFromSystem(new Vector3f(entity.getPosition().getX(), entity.getPosition().getFloorY(), entity.getPosition().getZ()));
                entity.setMotion(Vector3f.ZERO);
            } else {
                entity.setMotion(entity.getMotion().mul(1f - physics.getDrag()));
                if (!onGroundNow) {
                    entity.setMotion(entity.getMotion().sub(0, physics.getGravity(), 0));
                }
            }
        } else if (!entity.getMotion().equals(Vector3f.ZERO)) {
            entity.setPositionFromSystem(entity.getPosition().add(entity.getMotion()));
            entity.setMotion(Vector3f.ZERO);
        }
    }
}

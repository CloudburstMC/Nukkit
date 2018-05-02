package com.nukkitx.server.entity.misc;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.component.Explode;
import com.nukkitx.api.entity.component.Physics;
import com.nukkitx.api.entity.misc.PrimedTNT;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.component.ExplodableComponent;
import com.nukkitx.server.entity.component.PhysicsComponent;
import com.nukkitx.server.level.NukkitLevel;

public class PrimedTNTEntity extends BaseEntity implements PrimedTNT {

    public PrimedTNTEntity(Vector3f position, NukkitLevel level, NukkitServer server, int fuse, int radius, boolean incendinary) {
        super(EntityType.PRIMED_TNT, position, level, server);
        // Default fuse 80
        registerComponent(Explode.class, new ExplodableComponent(fuse, radius, incendinary));
        registerComponent(Physics.class, new PhysicsComponent(0.04f, 0.02f));
    }
}
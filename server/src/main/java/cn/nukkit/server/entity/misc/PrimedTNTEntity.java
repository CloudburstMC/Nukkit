package cn.nukkit.server.entity.misc;

import cn.nukkit.api.entity.component.Explode;
import cn.nukkit.api.entity.component.Physics;
import cn.nukkit.api.entity.misc.PrimedTNT;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.component.ExplodableComponent;
import cn.nukkit.server.entity.component.PhysicsComponent;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class PrimedTNTEntity extends BaseEntity implements PrimedTNT {

    public PrimedTNTEntity(Vector3f position, NukkitLevel level, NukkitServer server, int fuse, int radius, boolean incendinary) {
        super(EntityType.PRIMED_TNT, position, level, server);
        // Default fuse 80
        registerComponent(Explode.class, new ExplodableComponent(fuse, radius, incendinary));
        registerComponent(Physics.class, new PhysicsComponent(0.04f, 0.02f));
    }
}
package cn.nukkit.server.entity.misc;

import cn.nukkit.api.entity.component.Physics;
import cn.nukkit.api.entity.misc.FallingBlock;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.component.PhysicsComponent;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class FallingBlockEntity extends BaseEntity implements FallingBlock {

    public FallingBlockEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.FALLING_BLOCK, position, level, server);

        registerComponent(Physics.class, new PhysicsComponent(0.04f, 0.02f));
        // ContainedBlock.class
    }
}

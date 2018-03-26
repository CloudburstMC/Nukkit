package cn.nukkit.server.entity.monster;

import cn.nukkit.api.entity.monster.MagmaCube;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class MagmaCubeEntity extends LivingEntity implements MagmaCube {

    public MagmaCubeEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.MAGMA_CUBE, position, level, server, 16);
    }
}

package cn.nukkit.server.entity.passive;

import cn.nukkit.api.entity.passive.Llama;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.LivingEntity;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3f;

public class LlamaEntity extends LivingEntity implements Llama {

    public LlamaEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.LLAMA, position, level, server, 15);
    }
}

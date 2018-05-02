package com.nukkitx.server.entity.passive;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.passive.Llama;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.LivingEntity;
import com.nukkitx.server.level.NukkitLevel;

public class LlamaEntity extends LivingEntity implements Llama {

    public LlamaEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.LLAMA, position, level, server, 15);
    }
}

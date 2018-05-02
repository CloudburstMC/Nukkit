package com.nukkitx.server.entity.misc;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.misc.Painting;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.packet.AddPaintingPacket;

public class PaintingEntity extends BaseEntity implements Painting {

    public PaintingEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.PAINTING, position, level, server);
    }

    @Override
    public MinecraftPacket createAddEntityPacket() {
        AddPaintingPacket packet = new AddPaintingPacket();
        packet.setUniqueEntityId(getEntityId());
        packet.setRuntimeEntityId(getEntityId());
        packet.setBlockPosition(getPosition().toInt());
        packet.setTitle(Type.ALBAN.getName());
        return packet;
    }
}

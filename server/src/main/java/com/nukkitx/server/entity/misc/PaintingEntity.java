package com.nukkitx.server.entity.misc;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.misc.Painting;
import com.nukkitx.protocol.bedrock.packet.AddPaintingPacket;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.level.NukkitLevel;

public class PaintingEntity extends BaseEntity implements Painting {

    public PaintingEntity(Vector3f position, NukkitLevel level, NukkitServer server) {
        super(EntityType.PAINTING, position, level, server);
    }

    @Override
    public AddPaintingPacket createAddEntityPacket() {
        AddPaintingPacket packet = new AddPaintingPacket();
        packet.setUniqueEntityId(getEntityId());
        packet.setRuntimeEntityId(getEntityId());
        packet.setPosition(getPosition());
        packet.setName(Type.ALBAN.getName());
        return packet;
    }
}

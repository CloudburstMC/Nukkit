package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.util.Rotation;
import com.nukkitx.server.entity.Attribute;
import com.nukkitx.server.entity.EntityLink;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import com.nukkitx.server.network.bedrock.util.MetadataDictionary;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.nukkitx.server.network.bedrock.BedrockUtil.*;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
public class AddEntityPacket implements BedrockPacket {
    private long uniqueEntityId;
    private long runtimeEntityId;
    private int entityType;
    private Vector3f position;
    private Vector3f motion;
    private Rotation rotation;
    private final List<Attribute> entityAttributes = new ArrayList<>();
    private final MetadataDictionary metadata = new MetadataDictionary();
    private final List<EntityLink> links = new ArrayList<>();

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, uniqueEntityId);
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeUnsignedInt(buffer, entityType);
        writeVector3f(buffer, position);
        writeVector3f(buffer, motion);
        writeVector2f(buffer, rotation.getBodyRotation());
        writeEntityAttributes(buffer, entityAttributes);
        metadata.writeTo(buffer);
        writeEntityLinks(buffer, links);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // This packet isn't handled
    }
}

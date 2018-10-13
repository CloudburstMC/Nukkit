package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.util.Identifier;
import com.nukkitx.api.util.Rotation;
import com.nukkitx.server.entity.Attribute;
import com.nukkitx.server.entity.EntityLink;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import com.nukkitx.server.network.bedrock.util.MetadataDictionary;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.nukkitx.server.network.bedrock.BedrockUtil.*;

@Data
public class AddEntityPacket implements BedrockPacket {
    private long uniqueEntityId;
    private long runtimeEntityId;
    private Identifier identifier;
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
        writeString(buffer, identifier.getFullName());
        writeVector3f(buffer, position);
        writeVector3f(buffer, motion);
        writeRotation(buffer, rotation);
        writeEntityAttributes(buffer, entityAttributes);
        metadata.writeTo(buffer);
        writeEntityLinks(buffer, links);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        // This packet isn't handled
    }
}

package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.entity.Attribute;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writePlayerAttributes;
import static com.nukkitx.server.network.bedrock.BedrockUtil.writeRuntimeEntityId;

@Data
public class UpdateAttributesPacket implements BedrockPacket {
    private long runtimeEntityId;
    private List<Attribute> attributes = new ArrayList<>();

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writePlayerAttributes(buffer, attributes);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        // Only client bound.
    }
}

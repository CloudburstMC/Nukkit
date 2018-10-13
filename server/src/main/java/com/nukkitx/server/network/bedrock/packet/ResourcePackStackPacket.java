package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.api.resourcepack.ResourcePack;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writePackInstanceEntries;

@Data
public class ResourcePackStackPacket implements BedrockPacket {
    private boolean forcedToAccept;
    private final List<ResourcePack> behaviorPacks = new ArrayList<>();
    private final List<ResourcePack> resourcePacks = new ArrayList<>();
    private boolean experimental;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBoolean(forcedToAccept);
        writePackInstanceEntries(buffer, behaviorPacks);
        writePackInstanceEntries(buffer, resourcePacks);
        buffer.writeBoolean(experimental);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        // Only client bound
    }
}

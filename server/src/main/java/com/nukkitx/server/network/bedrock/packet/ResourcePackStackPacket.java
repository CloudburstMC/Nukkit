package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.api.resourcepack.ResourcePack;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writePackInstanceEntries;

@Data
public class ResourcePackStackPacket implements BedrockPacket {
    private final List<ResourcePack> behaviorPacks = new ArrayList<>();
    private final List<ResourcePack> resourcePacks = new ArrayList<>();
    private boolean forcedToAccept;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBoolean(forcedToAccept);
        writePackInstanceEntries(buffer, behaviorPacks);
        writePackInstanceEntries(buffer, resourcePacks);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound
    }
}

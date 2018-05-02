package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.api.resourcepack.ResourcePack;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.nukkitx.server.network.minecraft.MinecraftUtil.writePackInfoEntries;

@Data
public class ResourcePacksInfoPacket implements MinecraftPacket {
    private final List<ResourcePack> behaviorPackInfos = new ArrayList<>();
    private final List<ResourcePack> resourcePackInfos = new ArrayList<>();
    private boolean forcedToAccept;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBoolean(forcedToAccept);
        writePackInfoEntries(buffer, behaviorPackInfos);
        writePackInfoEntries(buffer, resourcePackInfos);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }
}

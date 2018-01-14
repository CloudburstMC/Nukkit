package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.resourcepack.ResourcePack;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.writePackInstanceEntries;

@Data
public class ResourcePackStackPacket implements MinecraftPacket {
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

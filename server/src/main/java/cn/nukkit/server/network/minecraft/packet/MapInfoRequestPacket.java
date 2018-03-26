package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.readUniqueEntityId;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeUniqueEntityId;

@Data
public class MapInfoRequestPacket implements MinecraftPacket {
    private long mapId;

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, mapId);
    }

    @Override
    public void decode(ByteBuf buffer) {
        mapId = readUniqueEntityId(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}

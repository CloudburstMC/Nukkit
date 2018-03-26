package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.util.GameMode;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.readSignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeSignedInt;

@Data
public class SetPlayerGameTypePacket implements MinecraftPacket {
    private GameMode gamemode;

    @Override
    public void encode(ByteBuf buffer) {
        writeSignedInt(buffer, gamemode.ordinal());
    }

    @Override
    public void decode(ByteBuf buffer) {
        gamemode = GameMode.parse(readSignedInt(buffer));
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}

package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.data.ScoreInfo;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeScoreInfo;

@Data
public class SetScorePacket implements MinecraftPacket {
    private byte unknownByte0;
    private List<ScoreInfo> info = new ArrayList<>();

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(unknownByte0);
        writeScoreInfo(buffer, info);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Client bound only
    }
}

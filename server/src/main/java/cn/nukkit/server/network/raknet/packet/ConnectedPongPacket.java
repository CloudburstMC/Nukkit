package cn.nukkit.server.network.raknet.packet;

import cn.nukkit.server.network.raknet.NetworkPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class ConnectedPongPacket implements NetworkPacket {
    private long pingTime;
    private long pongTime;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(pingTime);
        buffer.writeLong(pongTime);
    }

    @Override
    public void decode(ByteBuf buffer) {
        pingTime = buffer.readLong();
        pongTime = buffer.readLong();
    }
}

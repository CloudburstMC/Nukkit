package cn.nukkit.server.network.raknet.packet;

import cn.nukkit.server.network.raknet.NetworkPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class DisconnectNotificationPacket implements NetworkPacket {

    @Override
    public void encode(ByteBuf buffer) {
        // No payload
    }

    @Override
    public void decode(ByteBuf buffer) {
        // No payload
    }
}

package cn.nukkit.server.network.raknet.packet;

import cn.nukkit.server.network.raknet.NetworkPacket;
import cn.nukkit.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class OfflineMessage implements NetworkPacket {

    @Override
    public void encode(ByteBuf buffer) {
        RakNetUtil.writeUnconnectedMagic(buffer);
    }

    @Override
    public void decode(ByteBuf buffer) {
        RakNetUtil.verifyUnconnectedMagic(buffer);
    }
}

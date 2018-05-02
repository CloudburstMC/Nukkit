package com.nukkitx.server.network.raknet.packet;

import com.nukkitx.server.network.raknet.NetworkPacket;
import com.nukkitx.server.network.raknet.RakNetUtil;
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

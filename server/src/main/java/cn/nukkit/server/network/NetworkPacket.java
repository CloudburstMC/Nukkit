package cn.nukkit.server.network;

import io.netty.buffer.ByteBuf;

public interface NetworkPacket {

    void encode(ByteBuf buffer);

    void decode(ByteBuf buffer);

    void handle();
}

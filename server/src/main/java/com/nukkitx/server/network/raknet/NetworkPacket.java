package com.nukkitx.server.network.raknet;

import io.netty.buffer.ByteBuf;

public interface NetworkPacket {

    void encode(ByteBuf buffer);

    void decode(ByteBuf buffer);
}

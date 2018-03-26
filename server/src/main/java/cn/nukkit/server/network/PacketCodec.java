package cn.nukkit.server.network;

import cn.nukkit.server.network.raknet.NetworkPacket;
import io.netty.buffer.ByteBuf;

public interface PacketCodec<T extends NetworkPacket> {

    T tryDecode(ByteBuf byteBuf);

    ByteBuf tryEncode(T packet);
}

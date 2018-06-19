package com.nukkitx.server.network.bedrock.wrapper;

import com.nukkitx.network.PacketCodec;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import io.netty.buffer.ByteBuf;

import java.util.Collection;
import java.util.List;

public interface WrapperHandler {
    default ByteBuf compressPackets(PacketCodec<BedrockPacket> packetCodec, Collection<BedrockPacket> packets) {
        return compressPackets(packetCodec, packets.toArray(new BedrockPacket[0]));
    }

    ByteBuf compressPackets(PacketCodec<BedrockPacket> packetCodec, BedrockPacket... packets);

    List<BedrockPacket> decompressPackets(PacketCodec<BedrockPacket> packetCodec, ByteBuf compressed);
}
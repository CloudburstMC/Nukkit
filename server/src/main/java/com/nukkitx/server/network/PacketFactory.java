package com.nukkitx.server.network;

import com.nukkitx.server.network.raknet.NetworkPacket;

@FunctionalInterface
public interface PacketFactory<T extends NetworkPacket> {
    T newInstance();
}

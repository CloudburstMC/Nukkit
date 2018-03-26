package cn.nukkit.server.network;

import cn.nukkit.server.network.raknet.NetworkPacket;

@FunctionalInterface
public interface PacketFactory<T extends NetworkPacket> {
    T newInstance();
}

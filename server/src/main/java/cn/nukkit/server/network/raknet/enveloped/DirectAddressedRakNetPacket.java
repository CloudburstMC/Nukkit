package cn.nukkit.server.network.raknet.enveloped;

import cn.nukkit.server.network.raknet.NetworkPacket;
import io.netty.channel.DefaultAddressedEnvelope;

import java.net.InetSocketAddress;

public class DirectAddressedRakNetPacket extends DefaultAddressedEnvelope<NetworkPacket, InetSocketAddress> {
    public DirectAddressedRakNetPacket(NetworkPacket message, InetSocketAddress recipient, InetSocketAddress sender) {
        super(message, recipient, sender);
    }

    public DirectAddressedRakNetPacket(NetworkPacket message, InetSocketAddress recipient) {
        super(message, recipient);
    }
}

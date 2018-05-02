package com.nukkitx.server.network.raknet.enveloped;

import com.nukkitx.server.network.raknet.RakNetPacket;
import io.netty.channel.DefaultAddressedEnvelope;

import java.net.InetSocketAddress;

public class DirectAddressedRakNetPacket extends DefaultAddressedEnvelope<RakNetPacket, InetSocketAddress> {
    public DirectAddressedRakNetPacket(RakNetPacket message, InetSocketAddress recipient, InetSocketAddress sender) {
        super(message, recipient, sender);
    }

    public DirectAddressedRakNetPacket(RakNetPacket message, InetSocketAddress recipient) {
        super(message, recipient);
    }
}

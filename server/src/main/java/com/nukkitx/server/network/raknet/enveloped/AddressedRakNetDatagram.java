package com.nukkitx.server.network.raknet.enveloped;

import com.nukkitx.server.network.raknet.datagram.RakNetDatagram;
import io.netty.channel.DefaultAddressedEnvelope;

import java.net.InetSocketAddress;

public class AddressedRakNetDatagram extends DefaultAddressedEnvelope<RakNetDatagram, InetSocketAddress> {
    public AddressedRakNetDatagram(RakNetDatagram message, InetSocketAddress recipient, InetSocketAddress sender) {
        super(message, recipient, sender);
    }

    public AddressedRakNetDatagram(RakNetDatagram message, InetSocketAddress recipient) {
        super(message, recipient);
    }
}


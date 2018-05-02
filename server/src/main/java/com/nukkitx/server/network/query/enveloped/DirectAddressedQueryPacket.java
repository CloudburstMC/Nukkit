package com.nukkitx.server.network.query.enveloped;

import com.nukkitx.server.network.query.QueryPacket;
import io.netty.channel.DefaultAddressedEnvelope;

import java.net.InetSocketAddress;

public class DirectAddressedQueryPacket extends DefaultAddressedEnvelope<QueryPacket, InetSocketAddress> {
    public DirectAddressedQueryPacket(QueryPacket message, InetSocketAddress recipient, InetSocketAddress sender) {
        super(message, recipient, sender);
    }

    public DirectAddressedQueryPacket(QueryPacket message, InetSocketAddress recipient) {
        super(message, recipient);
    }
}

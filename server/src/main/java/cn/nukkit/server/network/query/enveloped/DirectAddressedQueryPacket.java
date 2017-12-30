package cn.nukkit.server.network.query.enveloped;

import cn.nukkit.server.network.query.QueryPacket;
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

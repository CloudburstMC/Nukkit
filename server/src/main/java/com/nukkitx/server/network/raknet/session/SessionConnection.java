package com.nukkitx.server.network.raknet.session;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.Optional;

public interface SessionConnection {

    Optional<InetSocketAddress> getRemoteAddress();

    void close();

    void sendPacket(@Nonnull ByteBuf data);

    boolean isClosed();

    void onTick();
}

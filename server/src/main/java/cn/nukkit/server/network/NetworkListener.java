package cn.nukkit.server.network;

import java.net.InetSocketAddress;

public interface NetworkListener {

    boolean bind();

    void close();

    InetSocketAddress getAddress();
}

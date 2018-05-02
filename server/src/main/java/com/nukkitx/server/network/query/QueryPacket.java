package com.nukkitx.server.network.query;

import com.nukkitx.server.network.raknet.NetworkPacket;

public interface QueryPacket extends NetworkPacket {

    int getSessionId();

    void setSessionId(int sessionId);

    short getId();
}

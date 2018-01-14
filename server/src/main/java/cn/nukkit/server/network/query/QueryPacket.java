package cn.nukkit.server.network.query;

import cn.nukkit.server.network.raknet.NetworkPacket;

public interface QueryPacket extends NetworkPacket {

    int getSessionId();

    void setSessionId(int sessionId);

    short getId();
}

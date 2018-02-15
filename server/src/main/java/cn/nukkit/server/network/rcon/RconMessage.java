package cn.nukkit.server.network.rcon;

import lombok.Value;

@Value
public class RconMessage {
    public static final int AUTH = 3;
    public static final int AUTH_RESPONSE = 2;
    public static final int EXECCOMMAND = 2;
    public static final int RESPONSE_VALUE = 0;

    private final int id;
    private final int type;
    private final String body;
}

package cn.nukkit.network.protocol.types;

import lombok.ToString;

import java.util.UUID;

/**
 * @author SupremeMortal
 * Nukkit project
 */
@ToString
public class CommandOriginData {

    public static final int ORIGIN_PLAYER = 0;
    public static final int ORIGIN_BLOCK = 1;
    public static final int ORIGIN_MINECART_BLOCK = 2;
    public static final int ORIGIN_DEV_CONSOLE = 3;
    public static final int ORIGIN_TEST = 4;
    public static final int ORIGIN_AUTOMATION_PLAYER = 5;
    public static final int ORIGIN_CLIENT_AUTOMATION = 6;
    public static final int ORIGIN_DEDICATED_SERVER = 7;
    public static final int ORIGIN_ENTITY = 8;
    public static final int ORIGIN_VIRTUAL = 9;
    public static final int ORIGIN_GAME_ARGUMENT = 10;
    public static final int ORIGIN_ENTITY_SERVER = 11;

    public int type;
    public UUID uuid;
    public String requestId;
    public long playerUniqueId;
}

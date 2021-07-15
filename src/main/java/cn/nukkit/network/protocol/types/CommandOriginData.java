package cn.nukkit.network.protocol.types;

import lombok.ToString;

import java.util.UUID;

/**
 * @author SupremeMortal
 * Nukkit project
 */
@ToString
public class CommandOriginData {

    public final Type type;
    public final UUID uuid;
    public final String requestId;
    public long entityUniqueId;

    public CommandOriginData(Type type, UUID uuid, String requestId) {
        this.type = type;
        this.uuid = uuid;
        this.requestId = requestId;
    }

    public static enum Type {

        PLAYER,
        BLOCK,
        MINECART_BLOCK,
        DEV_CONSOLE,
        TEST,
        AUTOMATION_PLAYER,
        CLIENT_AUTOMATION,
        DEDICATED_SERVER,
        ENTITY,
        VIRTUAL,
        GAME_ARGUMENT,
        ENTITY_SERVER;

        public static Type getById(int id) {
            return values()[id];
        }
    }
}

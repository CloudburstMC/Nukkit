package cn.nukkit.network.protocol.types;

import lombok.ToString;

import java.util.UUID;

/**
 * @author SupremeMortal
 * Nukkit project
 */
@ToString
public final class CommandOriginData {

    public final Origin type;
    public final UUID uuid;
    public final String requestId;
    public final Long playerId;

    public CommandOriginData(Origin type, UUID uuid, String requestId, Long playerId) {
        this.type = type;
        this.uuid = uuid;
        this.requestId = requestId;
        this.playerId = playerId;
    }

    public enum Origin {
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
        ENTITY_SERVER
    }
}

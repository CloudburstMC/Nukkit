package cn.nukkit.network.protocol.types;


import java.util.OptionalLong;
import java.util.UUID;

/**
 * @author SupremeMortal
 * Nukkit project
 */
public final class CommandOriginData {

    public final Origin type;
    public final UUID uuid;
    public final String requestId;
    private final Long varlong;

    public CommandOriginData(Origin type, UUID uuid, String requestId, Long varlong) {
        this.type = type;
        this.uuid = uuid;
        this.requestId = requestId;
        this.varlong = varlong;
    }

    public OptionalLong getVarLong() {
        if (varlong == null) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(varlong);
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

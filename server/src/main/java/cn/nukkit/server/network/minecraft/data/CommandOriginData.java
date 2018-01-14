package cn.nukkit.server.network.minecraft.data;

import lombok.experimental.Value;

import java.util.OptionalLong;
import java.util.UUID;

@Value
public class CommandOriginData {
    private Origin origin;
    private UUID uuid;
    private String requestId;
    private Long unknown0;

    public OptionalLong getUnknown0() {
        return (unknown0 == null) ? OptionalLong.empty() : OptionalLong.of(unknown0);
    }

    public enum Origin {
        PLAYER,
        COMMAND_BLOCK,
        MINECART_COMMAND_BLOCK,
        DEV_CONSOLE,
        TEST,
        AUTOMATION_PLAYER,
        CLIENT_AUTOMATION,
        DEDICATED_SERVER,
        ENTITY,
        VIRTUAL,
        GAME_ARGUMENT,
        INTERNAL
    }
}

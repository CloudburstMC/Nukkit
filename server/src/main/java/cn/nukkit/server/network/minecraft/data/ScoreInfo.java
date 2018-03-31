package cn.nukkit.server.network.minecraft.data;

import lombok.Value;

import java.util.UUID;

@Value
public class ScoreInfo {
    private final UUID uuid;
    private final String unknownString0;
    private final int unknownIntLE0;
}

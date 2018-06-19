package com.nukkitx.server.network.bedrock.data;

import lombok.Value;

import java.util.UUID;

@Value
public class ScoreInfo {
    private final UUID playerUuid;
    private final String objectiveId;
    private final int score;
}

package com.nukkitx.server.network.bedrock.data;

import com.nukkitx.server.scoreboard.NukkitScorer;
import lombok.Value;

@Value
public class ScoreInfo {
    private final NukkitScorer scorer;
    private final String objective;
    private final int score;
}

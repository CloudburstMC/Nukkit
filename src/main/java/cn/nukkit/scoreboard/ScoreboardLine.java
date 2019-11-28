package cn.nukkit.scoreboard;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Erik Miller
 * @version 1.0
 */
@Data
@AllArgsConstructor
class ScoreboardLine {
    private final byte type;
    private final long entityId;
    private final String fakeName;
    private final String objective;

    private int score;
}

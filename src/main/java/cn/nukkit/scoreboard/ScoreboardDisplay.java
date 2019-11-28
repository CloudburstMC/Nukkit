package cn.nukkit.scoreboard;

import cn.nukkit.entity.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Erik Miller
 * @version 1.0
 */
@AllArgsConstructor
@Data
public class ScoreboardDisplay {

    /**
     * The api is from the server software GoMint
     */

    private final Scoreboard scoreboard;
    private final String objectiveName;
    private String displayName;
    private SortOrder sortOrder;

    public DisplayEntry addEntity( Entity entity, int score ) {
        long scoreId = this.scoreboard.addOrUpdateEntity( entity, this.objectiveName, score );
        return new DisplayEntry( this.scoreboard, scoreId );
    }

    public DisplayEntry addLine( String line, int score ) {
        long scoreId = this.scoreboard.addOrUpdateLine( line, this.objectiveName, score );
        return new DisplayEntry( this.scoreboard, scoreId );
    }

    public void removeEntry( DisplayEntry entry ) {
        this.scoreboard.removeScoreEntry( entry.getScoreId() );
    }

}

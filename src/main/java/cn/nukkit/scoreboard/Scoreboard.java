package cn.nukkit.scoreboard;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.RemoveObjectivePacket;
import cn.nukkit.network.protocol.SetObjectivePacket;
import cn.nukkit.network.protocol.SetScorePacket;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

import java.util.*;

/**
 * @author Erik Miller
 * @version 1.0
 */
public class Scoreboard {

    /**
     * The api is from the server software GoMint
     */

    // Scores
    private long scoreIdCounter = 0;
    private Long2ObjectMap<ScoreboardLine> scoreboardLines = new Long2ObjectArrayMap<>();

    // Viewers
    private Set<Player> viewers = new HashSet<>();

    // Displays
    private Map<DisplaySlot, ScoreboardDisplay> displays = new EnumMap<>( DisplaySlot.class );

    public ScoreboardDisplay addDisplay( DisplaySlot slot, String objectiveName, String displayName ) {
        return this.addDisplay( slot, objectiveName, displayName, SortOrder.ASCENDING );
    }

    public ScoreboardDisplay addDisplay( DisplaySlot slot, String objectiveName, String displayName, SortOrder sortOrder ) {
        ScoreboardDisplay scoreboardDisplay = this.displays.get( slot );
        if ( scoreboardDisplay == null ) {
            scoreboardDisplay = new ScoreboardDisplay( this, objectiveName, displayName, sortOrder );
            this.displays.put( slot, scoreboardDisplay );

            this.broadcast( this.constructDisplayPacket( slot, scoreboardDisplay ) );
        }

        return scoreboardDisplay;
    }

    public void removeDisplay( DisplaySlot slot ) {
        ScoreboardDisplay display = this.displays.remove( slot );
        if ( display != null ) {
            // Remove all scores on this display
            LongList validScoreIDs = new LongArrayList();

            // Map fake entries first
            Long2ObjectMap.FastEntrySet<ScoreboardLine> fastSet = (Long2ObjectMap.FastEntrySet<ScoreboardLine>) this.scoreboardLines.long2ObjectEntrySet();
            ObjectIterator<Long2ObjectMap.Entry<ScoreboardLine>> fastIterator = fastSet.fastIterator();
            while ( fastIterator.hasNext() ) {
                Long2ObjectMap.Entry<ScoreboardLine> entry = fastIterator.next();
                if ( entry.getValue().getObjective().equals( display.getObjectiveName() ) ) {
                    validScoreIDs.add( entry.getLongKey() );
                }
            }

            // Remove all scores
            for ( long scoreID : validScoreIDs ) {
                this.scoreboardLines.remove( scoreID );
            }

            this.broadcast( this.constructRemoveScores( validScoreIDs ) );

            // Now get rid of the objective
            this.broadcast( this.constructRemoveDisplayPacket( display ) );
        }
    }

    private DataPacket constructDisplayPacket( DisplaySlot slot, ScoreboardDisplay display ) {
        SetObjectivePacket packetSetObjective = new SetObjectivePacket();
        packetSetObjective.setCriteriaName( "dummy" );
        packetSetObjective.setDisplayName( display.getDisplayName() );
        packetSetObjective.setObjectiveName( display.getObjectiveName() );
        packetSetObjective.setDisplaySlot( slot.name().toLowerCase() );
        packetSetObjective.setSortOrder( display.getSortOrder().ordinal() );
        return packetSetObjective;
    }

    private void broadcast( DataPacket packet ) {
        for ( Player viewer : this.viewers ) {
            viewer.dataPacket( packet );
        }
    }

    long addOrUpdateLine( String line, String objective, int score ) {
        // Check if we already have this registered
        Long2ObjectMap.FastEntrySet<ScoreboardLine> fastEntrySet = (Long2ObjectMap.FastEntrySet<ScoreboardLine>) this.scoreboardLines.long2ObjectEntrySet();
        ObjectIterator<Long2ObjectMap.Entry<ScoreboardLine>> fastIterator = fastEntrySet.fastIterator();
        while ( fastIterator.hasNext() ) {
            Long2ObjectMap.Entry<ScoreboardLine> entry = fastIterator.next();
            if ( entry.getValue().getType() == 3 && entry.getValue().getFakeName().equals( line ) && entry.getValue().getObjective().equals( objective ) ) {
                return entry.getLongKey();
            }
        }

        // Add this score
        long newId = this.scoreIdCounter++;
        ScoreboardLine scoreboardLine = new ScoreboardLine( (byte) 3, 0, line, objective, score );
        this.scoreboardLines.put( newId, scoreboardLine );

        // Broadcast entry
        this.broadcast( this.constructSetScore( newId, scoreboardLine ) );
        return newId;
    }

    long addOrUpdateEntity( Entity entity, String objective, int score ) {
        // Check if we already have this registered
        Long2ObjectMap.FastEntrySet<ScoreboardLine> fastEntrySet = (Long2ObjectMap.FastEntrySet<ScoreboardLine>) this.scoreboardLines.long2ObjectEntrySet();
        ObjectIterator<Long2ObjectMap.Entry<ScoreboardLine>> fastIterator = fastEntrySet.fastIterator();
        while ( fastIterator.hasNext() ) {
            Long2ObjectMap.Entry<ScoreboardLine> entry = fastIterator.next();
            if ( entry.getValue().getEntityId() == entity.getId() && entry.getValue().getObjective().equals( objective ) ) {
                return entry.getLongKey();
            }
        }

        // Add this score
        long newId = this.scoreIdCounter++;
        ScoreboardLine scoreboardLine = new ScoreboardLine( (byte) ( ( entity instanceof Player ) ? 1 : 2 ), entity.getId(), "", objective, score );
        this.scoreboardLines.put( newId, scoreboardLine );

        // Broadcast entry
        this.broadcast( this.constructSetScore( newId, scoreboardLine ) );

        return newId;
    }

    private DataPacket constructSetScore( long newId, ScoreboardLine line ) {
        SetScorePacket setScorePacket = new SetScorePacket();
        setScorePacket.setType( (byte) 0 );

        setScorePacket.setEntries( new ArrayList<SetScorePacket.ScoreEntry>() {{
            add( new SetScorePacket.ScoreEntry( newId, line.getObjective(), line.getScore(), line.getType(), line.getFakeName(), line.getEntityId() ) );
        }} );

        return setScorePacket;
    }

    private DataPacket constructSetScore() {
        SetScorePacket setScorePacket = new SetScorePacket();
        setScorePacket.setType( (byte) 0 );

        List<SetScorePacket.ScoreEntry> entries = new ArrayList<>();
        Long2ObjectMap.FastEntrySet<ScoreboardLine> fastEntrySet = (Long2ObjectMap.FastEntrySet<ScoreboardLine>) this.scoreboardLines.long2ObjectEntrySet();
        ObjectIterator<Long2ObjectMap.Entry<ScoreboardLine>> fastIterator = fastEntrySet.fastIterator();
        while ( fastIterator.hasNext() ) {
            Long2ObjectMap.Entry<ScoreboardLine> entry = fastIterator.next();
            entries.add( new SetScorePacket.ScoreEntry( entry.getLongKey(), entry.getValue().getObjective(), entry.getValue().getScore(), entry.getValue().getType(), entry.getValue().getFakeName(), entry.getValue().getEntityId() ) );
        }

        setScorePacket.setEntries( entries );
        return setScorePacket;
    }

    public void showFor( Player player ) {
        if ( this.viewers.add( player ) ) {
            // We send display information first
            for ( Map.Entry<DisplaySlot, ScoreboardDisplay> entry : this.displays.entrySet() ) {
                player.dataPacket( this.constructDisplayPacket( entry.getKey(), entry.getValue() ) );
            }

            // Send scores
            player.dataPacket( this.constructSetScore() );
        }
    }

    public void hideFor( Player player ) {
        if ( this.viewers.remove( player ) ) {
            // Remove all known scores
            LongList validScoreIDs = new LongArrayList();

            // Map fake entries first
            Long2ObjectMap.FastEntrySet<ScoreboardLine> fastSet = (Long2ObjectMap.FastEntrySet<ScoreboardLine>) this.scoreboardLines.long2ObjectEntrySet();
            ObjectIterator<Long2ObjectMap.Entry<ScoreboardLine>> fastIterator = fastSet.fastIterator();
            while ( fastIterator.hasNext() ) {
                validScoreIDs.add( fastIterator.next().getLongKey() );
            }

            // Remove all scores
            player.dataPacket( this.constructRemoveScores( validScoreIDs ) );

            // Remove all known displays
            for ( Map.Entry<DisplaySlot, ScoreboardDisplay> entry : this.displays.entrySet() ) {
                player.dataPacket( this.constructRemoveDisplayPacket( entry.getValue() ) );
            }
        }
    }

    private DataPacket constructRemoveScores( LongList scoreIDs ) {
        SetScorePacket setScorePacket = new SetScorePacket();
        setScorePacket.setType( (byte) 1 );

        List<SetScorePacket.ScoreEntry> entries = new ArrayList<>();
        for ( long scoreID : scoreIDs ) {
            entries.add( new SetScorePacket.ScoreEntry( scoreID, "", 0 ) );
        }

        setScorePacket.setEntries( entries );
        return setScorePacket;
    }

    private DataPacket constructRemoveDisplayPacket( ScoreboardDisplay display ) {
        RemoveObjectivePacket removeObjectivePacket = new RemoveObjectivePacket();
        removeObjectivePacket.setObjectiveName( display.getObjectiveName() );
        return removeObjectivePacket;
    }

    public void updateScore( long scoreId, int score ) {
        ScoreboardLine line = this.scoreboardLines.get( scoreId );
        if ( line != null ) {
            line.setScore( score );

            this.broadcast( this.constructSetScore( scoreId, line ) );
        }
    }

    public void removeScoreEntry( long scoreId ) {
        ScoreboardLine line = this.scoreboardLines.remove( scoreId );
        if ( line != null ) {
            this.broadcast( this.constructRemoveScores( scoreId ) );
        }
    }

    private DataPacket constructRemoveScores( long scoreId ) {
        SetScorePacket setScorePacket = new SetScorePacket();
        setScorePacket.setType( (byte) 1 );
        setScorePacket.setEntries( new ArrayList<SetScorePacket.ScoreEntry>() {{
            add( new SetScorePacket.ScoreEntry( scoreId, "", 0 ) );
        }} );
        return setScorePacket;
    }

    public int getScore( long scoreId ) {
        ScoreboardLine line = this.scoreboardLines.remove( scoreId );
        if ( line != null ) {
            return line.getScore();
        }

        return 0;
    }

}
package cn.nukkit.network.protocol;

import lombok.*;

import java.util.List;

/**
 * @author Erik Miller
 * @version 1.0
 */
@EqualsAndHashCode( callSuper = true )
@Data
public class SetScorePacket extends DataPacket {

    public static final byte NETWORK_ID = 0x6c;

    private byte type;
    private List<ScoreEntry> entries;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        //Ignore
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte( this.type );
        this.putUnsignedVarInt( this.entries.size() );

        for ( ScoreEntry entry : this.entries ) {
            this.putVarLong( entry.scoreId );
            this.putString( entry.objective );
            this.putLInt( entry.score );

            if(this.type == 0){
                this.putByte( entry.entityType );

                switch ( entry.entityType ) {
                    case 3: // Fake entity
                        this.putString( entry.fakeEntity );
                        break;
                    case 1:
                    case 2:
                        this.putUnsignedVarLong( entry.entityId );
                        break;
                }

            }
        }
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class ScoreEntry {
        private final long scoreId;
        private final String objective;
        private final int score;

        // Add entity type
        private byte entityType;
        private String fakeEntity;
        private long entityId;
    }
}
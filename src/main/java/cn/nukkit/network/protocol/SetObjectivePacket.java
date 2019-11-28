package cn.nukkit.network.protocol;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Erik Miller
 * @version 1.0
 */
@EqualsAndHashCode( callSuper = true )
@Data
public class SetObjectivePacket extends DataPacket {

    public static final byte NETWORK_ID = 0x6b;

    public String displaySlot;
    public String objectiveName;
    public String displayName;
    public String criteriaName;
    public int sortOrder;

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
        this.putString( this.displaySlot );
        this.putString( this.objectiveName );
        this.putString( this.displayName );
        this.putString( this.criteriaName );
        this.putVarInt( this.sortOrder );
    }
}
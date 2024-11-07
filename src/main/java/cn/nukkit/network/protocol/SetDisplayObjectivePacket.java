package cn.nukkit.network.protocol;

import cn.nukkit.scoreboard.Scoreboard;
import lombok.ToString;

@ToString
public class SetDisplayObjectivePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_DISPLAY_OBJECTIVE_PACKET;

    public Scoreboard.DisplaySlot displaySlot;
    public String objectiveId;
    public String displayName;
    public String criteria;
    public Scoreboard.SortOrder sortOrder;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.displaySlot.getType());
        this.putString(this.objectiveId);
        this.putString(this.displayName);
        this.putString(this.criteria);
        this.putVarInt(this.sortOrder.ordinal());
    }
}

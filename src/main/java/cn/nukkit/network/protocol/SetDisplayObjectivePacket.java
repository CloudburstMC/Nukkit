package cn.nukkit.network.protocol;

public class SetDisplayObjectivePacket extends DataPacket {

    public String displaySlot;
    public String objectiveId;
    public String displayName;
    public String criteria;
    public int sortOrder;

    @Override
    public byte pid() {
        return ProtocolInfo.SET_DISPLAY_OBJECTIVE_PACKET;
    }

    @Override
    public void decode() {
        this.displaySlot = this.getString();
        this.objectiveId = this.getString();
        this.displayName = this.getString();
        this.criteria = this.getString();
        this.sortOrder = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.displaySlot);
        this.putString(this.objectiveId);
        this.putString(this.displayName);
        this.putString(this.criteria);
        this.putVarInt(this.sortOrder);
    }

}

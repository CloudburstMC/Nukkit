package cn.nukkit.network.protocol;

public class RemoveObjectivePacket extends DataPacket {

    public String objectiveId;

    @Override
    public byte pid() {
        return ProtocolInfo.REMOVE_OBJECTIVE_PACKET;
    }

    @Override
    public void decode() {
        this.objectiveId = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.objectiveId);
    }

}

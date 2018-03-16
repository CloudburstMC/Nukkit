package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class UpdateEquipmentPacket extends DataPacket {

    public int windowId;
    public int windowType;
    public int unknown; //TODO: find out what this is (vanilla always sends 0)
    public long eid;
    public byte[] namedtag;


    @Override
    public byte pid() {
        return ProtocolInfo.UPDATE_EQUIPMENT_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.windowId);
        this.putByte((byte) this.windowType);
        this.putEntityUniqueId(this.eid);
        this.put(this.namedtag);
    }

    @Override
    protected void handle(Player player) {

    }
}

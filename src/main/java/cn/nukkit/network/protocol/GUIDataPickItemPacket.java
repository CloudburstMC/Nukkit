package cn.nukkit.network.protocol;

public class GUIDataPickItemPacket extends DataPacket {

    public int hotbarSlot;

    @Override
    public byte pid() {
        return ProtocolInfo.GUI_DATA_PICK_ITEM_PACKET;
    }

    @Override
    public void encode() {
        this.reset();
        this.putLInt(this.hotbarSlot);
    }

    @Override
    public void decode() {
        this.hotbarSlot = this.getLInt();
    }
}

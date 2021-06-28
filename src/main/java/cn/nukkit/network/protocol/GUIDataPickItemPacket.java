package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class GuiDataPickItemPacket extends DataPacket {

    public String itemDescription;
    public String itemEffects;
    public int hotbarSlot;

    @Override
    public byte pid() {
        return ProtocolInfo.GUI_DATA_PICK_ITEM_PACKET;
    }

    @Override
    public void decode() {
        this.itemDescription = this.getString();
        this.itemEffects = this.getString();
        this.hotbarSlot = this.getLInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.itemDescription);
        this.putString(this.itemEffects);
        this.putLInt(this.hotbarSlot);
    }
}

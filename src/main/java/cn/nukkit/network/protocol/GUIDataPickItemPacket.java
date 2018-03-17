package cn.nukkit.network.protocol;

import cn.nukkit.Player;

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

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}

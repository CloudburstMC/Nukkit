package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.ContainerIds;
import lombok.ToString;

@ToString
public class PlayerHotbarPacket extends DataPacket {

    public int selectedHotbarSlot;
    public byte windowId = ContainerIds.INVENTORY;
    public boolean selectHotbarSlot = true;

    @Override
    public byte pid() {
        return ProtocolInfo.PLAYER_HOTBAR_PACKET;
    }

    @Override
    public void decode() {
        this.selectedHotbarSlot = this.getUnsignedVarInt();
        this.windowId = this.getByte();
        this.selectHotbarSlot = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.selectedHotbarSlot);
        this.putByte(this.windowId);
        this.putBoolean(this.selectHotbarSlot);
    }
}

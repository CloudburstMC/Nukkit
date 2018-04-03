package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.utils.Binary;

public class PlayerHotbarPacket extends DataPacket {

    public int selectedHotbarSlot;
    public int windowId = ContainerIds.INVENTORY;

    public int[] slots;

    public boolean selectHotbarSlot = true;

    @Override
    public byte pid() {
        return ProtocolInfo.PLAYER_HOTBAR_PACKET;
    }

    @Override
    public void decode() {
        this.selectedHotbarSlot = (int) this.getUnsignedVarInt();
        this.windowId = this.getByte();
        int count = (int) this.getUnsignedVarInt();
        slots = new int[count];

        for (int i = 0; i < count; ++i) {
            this.slots[i] = Binary.signInt((int) this.getUnsignedVarInt());
        }
        this.selectHotbarSlot = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.selectedHotbarSlot);
        this.putByte((byte) this.windowId);
        this.putUnsignedVarInt(this.slots.length);
        for (int i : slots) {
            this.putUnsignedVarInt(i);
        }

        this.putBoolean(this.selectHotbarSlot);
    }
}

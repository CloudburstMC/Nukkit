package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class PlayerStartItemCooldownPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_START_ITEM_COOLDOWN_PACKET;

    public String itemCategory;
    public int cooldownDuration;

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
        this.putString(this.itemCategory);
        this.putVarInt(this.cooldownDuration);
    }
}

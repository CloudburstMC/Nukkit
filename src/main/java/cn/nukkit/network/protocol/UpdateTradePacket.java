package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class UpdateTradePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.UPDATE_TRADE_PACKET;

    public byte windowId;
    public byte windowType = 15; //Trading id
    public int windowSlotCount;
    public int tradeTier;
    public long traderUniqueId;
    public long playerUniqueId;
    public String displayName;
    public boolean isV2Trading;
    public boolean isWilling;
    public byte[] offers;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.windowId = (byte) this.getByte();
        this.windowType = (byte) this.getByte();
        this.windowSlotCount = this.getVarInt();
        this.tradeTier = this.getVarInt();
        this.traderUniqueId = this.getEntityUniqueId();
        this.playerUniqueId = this.getEntityUniqueId();
        this.displayName = this.getString();
        this.isV2Trading = this.getBoolean();
        this.isWilling = this.getBoolean();
        this.offers = this.get();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.windowId);
        this.putByte(this.windowType);
        this.putVarInt(this.windowSlotCount);
        this.putVarInt(this.tradeTier);
        this.putEntityUniqueId(this.traderUniqueId);
        this.putEntityUniqueId(this.playerUniqueId);
        this.putString(this.displayName);
        this.putBoolean(this.isV2Trading);
        this.putBoolean(this.isWilling);
        this.put(this.offers);
    }
}

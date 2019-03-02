package cn.nukkit.network.protocol;

public class UpdateTradePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.UPDATE_TRADE_PACKET;

    public byte windowId;
    public byte windowType = 15; //trading id
    public int unknownVarInt1;
    public int unknownVarInt2;
    public int unknownVarInt3;
    public boolean isWilling;
    public long trader;
    public long player;
    public String displayName;
    public byte[] offers;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.windowId = (byte) this.getByte();
        this.windowType = (byte) this.getByte();
        this.unknownVarInt1 = this.getVarInt();
        this.unknownVarInt2 = this.getVarInt();
        this.unknownVarInt3 = this.getVarInt();
        this.isWilling = this.getBoolean();
        this.player = this.getEntityUniqueId();
        this.trader = this.getEntityUniqueId();
        this.displayName = this.getString();
        this.offers = this.get();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(windowId);
        this.putByte(windowType);
        this.putVarInt(unknownVarInt1);
        this.putVarInt(unknownVarInt2);
        this.putVarInt(unknownVarInt3);
        this.putBoolean(isWilling);
        this.putEntityUniqueId(player);
        this.putEntityUniqueId(trader);
        this.putString(displayName);
        this.put(this.offers);
    }

}

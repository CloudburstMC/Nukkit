package cn.nukkit.network.protocol;

public class UpdateTradePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.UPDATE_TRADE_PACKET;

    public byte unknownByte1;
    public byte unknownByte2;
    public int unknownVarInt1;
    public int unknownVarInt2;
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

    }

    @Override
    public void encode() {
        this.putByte(unknownByte1);
        this.putByte(unknownByte2);
        this.putVarInt(unknownVarInt1);
        this.putVarInt(unknownVarInt2);
        this.putBoolean(isWilling);
        this.putVarLong(player);
        this.putVarLong(trader);
        this.putString(displayName);
        this.put(this.offers);
    }

}

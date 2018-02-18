package cn.nukkit.network.protocol;

public class UpdateTradePacket extends DataPacket {

    public byte windowId;
    public byte windowType = 15; //trading id
    public int unknownVarInt1;
    public int unknownVarInt2;
    public boolean isWilling;
    public long trader;
    public long player;
    public String displayName;
    public byte[] offers;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("UPDATE_TRADE_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putByte(windowId);
        this.putByte(windowType);
        this.putVarInt(unknownVarInt1);
        this.putVarInt(unknownVarInt2);
        this.putBoolean(isWilling);
        this.putEntityUniqueId(player);
        this.putEntityUniqueId(trader);
        this.putString(displayName);
        this.put(this.offers);
    }

}

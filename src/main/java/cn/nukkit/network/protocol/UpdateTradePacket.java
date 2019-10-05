package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class UpdateTradePacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.UPDATE_TRADE_PACKET;

    public byte windowId;
    public byte windowType = 15; //trading id
    public int unknownVarInt1; // hardcoded to 0
    public int tradeTier;
    public long trader;
    public long player;
    public String displayName;
    public boolean screen2;
    public boolean isWilling;
    public byte[] offers;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        buffer.writeByte(windowType);
        Binary.writeVarInt(buffer, unknownVarInt1);
        Binary.writeVarInt(buffer, tradeTier);
        Binary.writeEntityUniqueId(buffer, player);
        Binary.writeEntityUniqueId(buffer, trader);
        Binary.writeString(buffer, displayName);
        buffer.writeBoolean(screen2);
        buffer.writeBoolean(isWilling);
        buffer.writeBytes(this.offers);
    }

}

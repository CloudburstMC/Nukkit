package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class TransferPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.TRANSFER_PACKET;

    public String address; // Server address
    public int port = 19132; // Server port

    @Override
    protected void decode(ByteBuf buffer) {
        this.address = Binary.readString(buffer);
        this.port = buffer.readUnsignedShortLE();
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeString(buffer, address);
        buffer.writeShortLE(port);
    }

    @Override
    public short pid() {
        return ProtocolInfo.TRANSFER_PACKET;
    }
}

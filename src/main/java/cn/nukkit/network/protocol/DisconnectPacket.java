package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * Created by on 15-10-12.
 */
@ToString
public class DisconnectPacket extends DataPacket {
    public static final short NETWORK_ID = ProtocolInfo.DISCONNECT_PACKET;

    public boolean hideDisconnectionScreen = false;
    public String message;

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.hideDisconnectionScreen = buffer.readBoolean();
        this.message = Binary.readString(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeBoolean(this.hideDisconnectionScreen);
        if (!this.hideDisconnectionScreen) {
            Binary.writeString(buffer, this.message);
        }
    }


}

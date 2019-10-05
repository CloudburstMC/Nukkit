package cn.nukkit.network.protocol;

import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class PlayerInputPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.PLAYER_INPUT_PACKET;

    public float motionX;
    public float motionY;

    public boolean jumping;
    public boolean sneaking;

    @Override
    protected void decode(ByteBuf buffer) {
        this.motionX = buffer.readFloatLE();
        this.motionY = buffer.readFloatLE();
        this.jumping = buffer.readBoolean();
        this.sneaking = buffer.readBoolean();
    }

    @Override
    protected void encode(ByteBuf buffer) {

    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }

}

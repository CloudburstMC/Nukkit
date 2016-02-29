package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class PlayerInputPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_INPUT_PACKET;

    public float motionX;
    public float motionY;

    public boolean jumping;
    public boolean sneaking;

    @Override
    public void decode() {
        this.motionX = this.getFloat();
        this.motionY = this.getFloat();
        int flags = this.getByte();
        this.jumping = (flags & 0x80) > 0;
        this.sneaking = (flags & 0x40) > 0;
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}

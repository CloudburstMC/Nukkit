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
        this.motionX = this.getLFloat();
        this.motionY = this.getLFloat();
        this.jumping = this.getBoolean();
        this.sneaking = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putLFloat(this.motionX);
        this.putLFloat(this.motionY);
        this.putBoolean(this.jumping);
        this.putBoolean(this.sneaking);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}

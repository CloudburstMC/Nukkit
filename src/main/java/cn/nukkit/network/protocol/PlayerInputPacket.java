package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class PlayerInputPacket extends DataPacket {

    public float motionX;
    public float motionY;

    public boolean unknownBool1;
    public boolean unknownBool2;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("PLAYER_INPUT_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.motionX = this.getLFloat();
        this.motionY = this.getLFloat();
        this.unknownBool1 = this.getBoolean();
        this.unknownBool2 = this.getBoolean();
    }

    @Override
    public void encode(PlayerProtocol protocol) {

    }

}

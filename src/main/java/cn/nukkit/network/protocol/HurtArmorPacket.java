package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class HurtArmorPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.HURT_ARMOR_PACKET;

    public int health;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        reset();
        putVarInt(health);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}

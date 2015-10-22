package cn.nukkit.network.protocol;

/**
 * @author Nukkit Project Team
 */
public class SetHealthPacket extends DataPacket {

    public static final byte NETWORK_ID = Info.SET_HEALTH_PACKET;

    public int health;

    @Override
    public void decode() {
        health = getInt();
    }

    @Override
    public void encode() {
        reset();
        putInt(health);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}

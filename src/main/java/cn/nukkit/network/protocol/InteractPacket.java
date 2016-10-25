package cn.nukkit.network.protocol;

/**
 * Created on 15-10-15.
 */
public class InteractPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.INTERACT_PACKET;

    public static final byte ACTION_RIGHT_CLICK = 1;
    public static final byte ACTION_LEFT_CLICK = 2;
    public static final byte ACTION_VEHICLE_EXIT = 3;


    public long eid;
    public byte action;
    public long target;

    @Override
    public void decode() {
        action = (byte) getByte();
        target = getLong();
    }

    @Override
    public void encode() {
        reset();
        putByte(action);
        putLong(target);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}

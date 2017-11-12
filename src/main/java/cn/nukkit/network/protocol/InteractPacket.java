package cn.nukkit.network.protocol;

/**
 * Created on 15-10-15.
 */
public class InteractPacket extends DataPacket {

    //Only 1.1
    public static final int ACTION_RIGHT_CLICK = 1;
    public static final int ACTION_LEFT_CLICK = 2;
    //Everything
    public static final int ACTION_VEHICLE_EXIT = 3;
    public static final int ACTION_MOUSEOVER = 4;

    public static final int ACTION_OPEN_INVENTORY = 6;

    public int action;
    public long target;

    @Override
    public void decode(PlayerProtocol protocol) {
        this.action = this.getByte();
        this.target = this.getEntityRuntimeId();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putByte((byte) this.action);
        this.putEntityRuntimeId(this.target);
    }

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.INTERACT_PACKET :
                ProtocolInfo.INTERACT_PACKET;
    }

}

package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.3.0.0-PN")
public class PositionTrackingDBClientRequestPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.POS_TRACKING_CLIENT_REQUEST_PACKET;
    private static final Action[] ACTIONS = Action.values();
    
    private Action action;
    private int trackingId;

    @Override
    public void encode() {
        putByte((byte) action.ordinal());
        putVarInt(trackingId);
    }

    @Override
    public void decode() {
        action = ACTIONS[getByte()];
        trackingId = getVarInt();
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Since("1.3.0.0-PN")
    public enum Action {
        QUERY
    }
}

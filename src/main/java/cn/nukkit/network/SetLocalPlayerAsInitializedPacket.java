package cn.nukkit.network;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;

public class SetLocalPlayerAsInitializedPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.SET_LOCAL_PLAYER_AS_INITIALIZED_PACKET;

    public long eid;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        eid = this.getUnsignedVarLong();
    }

    @Override
    public void encode() {
        this.putUnsignedVarLong(eid);
    }
}

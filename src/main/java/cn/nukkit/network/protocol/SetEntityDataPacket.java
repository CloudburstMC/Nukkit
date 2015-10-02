package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;

import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SetEntityDataPacket extends DataPacket {
    public static final byte NETWORK_ID = Info.SET_ENTITY_DATA_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public long eid;
    public Map<Integer, Object[]> metadata;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putLong(this.eid);
        this.put(Binary.writeMetadata(this.metadata));
    }
}

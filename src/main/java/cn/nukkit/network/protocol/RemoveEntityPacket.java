package cn.nukkit.network.protocol;

import cn.nukkit.Player;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RemoveEntityPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.REMOVE_ENTITY_PACKET;

    public long eid;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.eid);
    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}

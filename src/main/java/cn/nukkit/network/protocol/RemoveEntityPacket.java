package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class RemoveEntityPacket extends DataPacket {

    public int entityNetworkId;

    @Override
    public byte pid() {
        return ProtocolInfo.REMOVE_ENTITY_PACKET;
    }

    @Override
    public void decode() {
    	this.entityNetworkId = this.getUnsignedVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.entityNetworkId);
    }
}

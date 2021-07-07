package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.EntityLink;
import lombok.ToString;

/**
 * Created on 15-10-22.
 */
@ToString
public class SetEntityLinkPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_ENTITY_LINK_PACKET;

    public EntityLink entityLink;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.entityLink = this.getEntityLink();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityLink(this.entityLink);
    }
}

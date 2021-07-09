package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class ContainerClosePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CONTAINER_CLOSE_PACKET;

    public byte windowId;
    public boolean wasServerInitiated = true;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.windowId = this.getByte();
        this.wasServerInitiated = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.windowId);
        this.putBoolean(this.wasServerInitiated);
    }
}

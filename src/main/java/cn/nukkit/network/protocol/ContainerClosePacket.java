package cn.nukkit.network.protocol;

import cn.nukkit.api.Since;
import lombok.ToString;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
public class ContainerClosePacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.CONTAINER_CLOSE_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public int windowId;
    @Since("1.4.0.0-PN") public boolean wasServerInitiated = true;

    @Override
    public void decode() {
        this.windowId = (byte) this.getByte();
        this.wasServerInitiated = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.windowId);
        this.putBoolean(this.wasServerInitiated);
    }
}

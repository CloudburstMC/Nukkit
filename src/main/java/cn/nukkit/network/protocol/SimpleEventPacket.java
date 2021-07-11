package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SimpleEventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SIMPLE_EVENT_PACKET;

    public static final int EVENT_ENABLE_COMMANDS = 1;
    public static final int EVENT_DISABLE_COMMANDS = 2;
    public static final int EVENT_UNLOCK_WORLD_TEMPLATE_SETTINGS = 3;

    public int event;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.event = this.getLShort();
    }

    @Override
    public void encode() {
        this.reset();
        this.putLShort(this.event);
    }
}

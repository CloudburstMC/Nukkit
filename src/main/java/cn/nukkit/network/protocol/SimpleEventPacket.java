package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class SimpleEventPacket extends DataPacket {

    public static final int TYPE_ENABLE_COMMANDS = 1;
    public static final int TYPE_DISABLE_COMMANDS = 2;
    public static final int TYPE_UNLOCK_WORLD_TEMPLATE_SETTINGS = 3;

    public int eventType;

    @Override
    public byte pid() {
        return ProtocolInfo.SIMPLE_EVENT_PACKET;
    }

    @Override
    public void decode() {
        this.eventType = this.getLShort();
    }

    @Override
    public void encode() {
        this.reset();
        this.putLShort(this.eventType);
    }
}

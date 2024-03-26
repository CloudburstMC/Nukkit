package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ScriptCustomEventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SCRIPT_CUSTOM_EVENT_PACKET;

    public String eventName;
    public byte[] eventData;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.eventName = this.getString();
        this.eventData = this.getByteArray();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.eventName);
        this.putByteArray(this.eventData);
    }
}

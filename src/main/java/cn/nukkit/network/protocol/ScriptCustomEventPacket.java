package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ScriptCustomEventPacket extends DataPacket {

    public String eventName;
    public String eventData;

    @Override
    public byte pid() {
        return ProtocolInfo.SCRIPT_CUSTOM_EVENT_PACKET;
    }

    @Override
    public void decode() {
        this.eventName = this.getString();
        this.eventData = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.eventName);
        this.putString(this.eventData);
    }
}

package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ScriptCustomEventPacket extends DataPacket {
    
    public String eventName;
    public byte[] eventData;

    @Override
    public byte pid() {
        return ProtocolInfo.SCRIPT_CUSTOM_EVENT_PACKET;
    }

    @Override
    public void decode() {
        eventName = getString();
        eventData = getByteArray();
    }

    @Override
    public void encode() {
        putString(eventName);
        putByteArray(eventData);
    }
}

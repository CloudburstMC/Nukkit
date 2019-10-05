package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString
public class ScriptCustomEventPacket extends DataPacket {
    
    public String eventName;
    public byte[] eventData;

    @Override
    public short pid() {
        return ProtocolInfo.SCRIPT_CUSTOM_EVENT_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.eventName = Binary.readString(buffer);
        this.eventData = Binary.readByteArray(buffer);
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeString(buffer, this.eventName);
        Binary.writeByteArray(buffer, this.eventData);
    }
}

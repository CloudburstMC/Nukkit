package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class CommandOutputPacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.COMMAND_OUTPUT_PACKET;
    }

    @Override
    public void decode() {
    	//TODO
    }

    @Override
    public void encode() {
        //TODO
    }
}

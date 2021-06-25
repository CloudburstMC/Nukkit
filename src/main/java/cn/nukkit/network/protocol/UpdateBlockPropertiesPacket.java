package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class UpdateBlockPropertiesPacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.UPDATE_BLOCK_PROPERTIES_PACKET;
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

package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class StructureTemplateDataResponsePacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.STRUCTURE_TEMPLATE_DATA_RESPONSE_PACKET;
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

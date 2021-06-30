package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ItemStackRequestPacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.ITEM_STACK_REQUEST_PACKET;
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

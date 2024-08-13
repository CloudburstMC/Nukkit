package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ClientboundCloseFormPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.__INTERNAL__CLIENTBOUND_CLOSE_FORM_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        this.reset();
        // No payload
    }
}

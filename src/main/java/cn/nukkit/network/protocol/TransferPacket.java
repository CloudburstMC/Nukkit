package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class TransferPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.TRANSFER_PACKET;

    public String address;
    public int port = 19132;

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(address);
        this.putLShort(port);
        this.putBoolean(false); // reloadWorld
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}

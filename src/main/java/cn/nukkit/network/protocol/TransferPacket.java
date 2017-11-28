package cn.nukkit.network.protocol;

// A wild TransferPacket appeared!
public class TransferPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.TRANSFER_PACKET;

    public String address; // Server address
    public int port = 19132; // Server port

    @Override
    public void decode() {
        this.address = this.getString();
        this.port = (short) this.getLShort();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(address);
        this.putLShort(port);
    }

    @Override
    public byte pid() {
        return ProtocolInfo.TRANSFER_PACKET;
    }
}

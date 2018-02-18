package cn.nukkit.network.protocol;

// A wild TransferPacket appeared!
public class TransferPacket extends DataPacket {

    public String address; // Server address
    public int port = 19132; // Server port

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("TRANSFER_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.address = this.getString();
        this.port = (short) this.getLShort();
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putString(address);
        this.putLShort(port);
    }

}

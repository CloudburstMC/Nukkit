package cn.nukkit.network.protocol;

public class ResourcePackClientResponsePacket extends DataPacket {

    public static final byte STATUS_REFUSED = 1;
    public static final byte STATUS_SEND_PACKS = 2;
    public static final byte STATUS_HAVE_ALL_PACKS = 3;
    public static final byte STATUS_COMPLETED = 4;

    public byte responseStatus;
    public String[] packIds;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("RESOURCE_PACK_CLIENT_RESPONSE_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.responseStatus = (byte) this.getByte();
        this.packIds = new String[this.getLShort()];
        for (int i = 0; i < this.packIds.length; i++) {
            this.packIds[i] = this.getString();
        }
    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putByte(this.responseStatus);
        this.putLShort(this.packIds.length);
        for (String id : this.packIds) {
            this.putString(id);
        }
    }

}

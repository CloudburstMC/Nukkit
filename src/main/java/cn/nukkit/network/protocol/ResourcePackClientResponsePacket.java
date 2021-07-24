package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class ResourcePackClientResponsePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET;

    public ResponseStatus status;
    public String[] packIds = new String[0];

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.status = ResponseStatus.values()[this.getByte()];
        int count = this.getLShort();
        this.packIds = new String[count];
        for (int i = 0; i < count; i++) {
            this.packIds[i] = this.getString();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.status.ordinal());
        this.putLShort(this.packIds.length);
        for (String packId : this.packIds) {
            this.putString(packId);
        }
    }

    public enum ResponseStatus {

        NONE,
        REFUSED,
        SEND_PACKS,
        HAVE_ALL_PACKS,
        COMPLETED
    }
}

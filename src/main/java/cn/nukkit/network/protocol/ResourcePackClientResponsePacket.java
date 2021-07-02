package cn.nukkit.network.protocol;

import lombok.ToString;

import java.util.UUID;

@ToString
public class ResourcePackClientResponsePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET;

    public ResponseStatus responseStatus;
    public Entry[] packEntries;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.responseStatus = ResponseStatus.values()[this.getByte() - 1];
        int count = this.getLShort();
        this.packEntries = new Entry[count];
        for (int i = 0; i < count; i++) {
            String[] entry = this.getString().split("_");
            this.packEntries[i] = new Entry(UUID.fromString(entry[0]), entry[1]);
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) (this.responseStatus.ordinal() + 1));
        this.putLShort(this.packEntries.length);
        for (Entry entry : this.packEntries) {
            this.putString(entry.uuid.toString() + '_' + entry.version);
        }
    }

    @ToString
    public static class Entry {

        public final UUID uuid;
        public final String version;

        public Entry(UUID uuid, String version) {
            this.uuid = uuid;
            this.version = version;
        }
    }

    public static enum ResponseStatus {

        REFUSED,
        SEND_PACKS,
        HAVE_ALL_PACKS,
        COMPLETED
    }
}

package cn.nukkit.network.protocol;

import lombok.ToString;

import java.util.UUID;

@ToString
public class ResourcePackClientResponsePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET;

    public static final byte STATUS_REFUSED = 1;
    public static final byte STATUS_SEND_PACKS = 2;
    public static final byte STATUS_HAVE_ALL_PACKS = 3;
    public static final byte STATUS_COMPLETED = 4;

    public byte responseStatus;
    public Entry[] packEntries;

    @Override
    public void decode() {
        this.responseStatus = (byte) this.getByte();
        this.packEntries = new Entry[this.getLShort()];
        for (int i = 0; i < this.packEntries.length; i++) {
            String[] entry = this.getString().split("_");
            this.packEntries[i] = new Entry(UUID.fromString(entry[0]), entry[1]);
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.responseStatus);
        this.putLShort(this.packEntries.length);
        for (Entry entry : this.packEntries) {
            this.putString(entry.uuid.toString() + '_' + entry.version);
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
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
}

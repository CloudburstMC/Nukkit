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
        int count = this.getLShort();
        if (count > 1024) throw new IllegalArgumentException("Too many entries");
        this.packEntries = new Entry[count];
        for (int i = 0; i < this.packEntries.length; i++) {
            String[] entry = this.getString().split("_", 3);

            String uuidString = entry[0];
            if (uuidString.length() > 36) {
                throw new IllegalArgumentException("Invalid packId");
            }

            this.packEntries[i] = new Entry(UUID.fromString(uuidString), entry[1]);
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

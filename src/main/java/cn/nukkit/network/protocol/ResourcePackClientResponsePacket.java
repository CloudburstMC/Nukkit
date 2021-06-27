package cn.nukkit.network.protocol;

import lombok.ToString;

import java.util.UUID;

@ToString
public class ResourcePackClientResponsePacket extends DataPacket {

    public static final byte STATUS_REFUSED = 1;
    public static final byte STATUS_SEND_PACKS = 2;
    public static final byte STATUS_HAVE_ALL_PACKS = 3;
    public static final byte STATUS_COMPLETED = 4;

    public byte status;
    public Entry[] packEntries;

    @Override
    public byte pid() {
        return ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET;
    }

    @Override
    public void decode() {
        this.status = this.getByte();
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
        this.putByte(this.status);
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
}

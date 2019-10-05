package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

import java.util.UUID;

@ToString
public class ResourcePackClientResponsePacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET;

    public static final byte STATUS_REFUSED = 1;
    public static final byte STATUS_SEND_PACKS = 2;
    public static final byte STATUS_HAVE_ALL_PACKS = 3;
    public static final byte STATUS_COMPLETED = 4;

    public byte responseStatus;
    public Entry[] packEntries;

    @Override
    protected void decode(ByteBuf buffer) {
        this.responseStatus = buffer.readByte();
        this.packEntries = new Entry[buffer.readShortLE()];
        for (int i = 0; i < this.packEntries.length; i++) {
            String[] entry = Binary.readString(buffer).split("_");
            this.packEntries[i] = new Entry(UUID.fromString(entry[0]), entry[1]);
        }
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeByte(this.responseStatus);
        buffer.writeShortLE(this.packEntries.length);
        for (Entry entry : this.packEntries) {
            Binary.writeString(buffer, entry.uuid.toString() + '_' + entry.version);
        }
    }

    @Override
    public short pid() {
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

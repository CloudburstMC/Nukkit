package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

import java.util.UUID;

@ToString
public class ResourcePackClientResponsePacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET;

    public Status status;
    public String[] packEntries;

    @Override
    protected void decode(ByteBuf buffer) {
        this.status = Status.values()[buffer.readUnsignedByte()];
        this.packEntries = new String[buffer.readShortLE()];
        for (int i = 0; i < this.packEntries.length; i++) {
            this.packEntries[i] = Binary.readString(buffer);
        }
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeByte(this.status.ordinal());
        buffer.writeShortLE(this.packEntries.length);
        for (String entry : this.packEntries) {
            Binary.writeString(buffer, entry);
        }
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }

    public enum Status {
        UNKNOWN,
        REFUSED,
        SEND_PACKS,
        HAVE_ALL_PACKS,
        COMPLETED
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

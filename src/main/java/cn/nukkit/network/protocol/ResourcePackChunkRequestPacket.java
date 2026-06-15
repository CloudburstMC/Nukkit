package cn.nukkit.network.protocol;

import lombok.ToString;

import java.util.UUID;

@ToString
public class ResourcePackChunkRequestPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET;

    public String version;
    public UUID packId;
    public int chunkIndex;

    @Override
    public void decode() {
        String[] parts = this.getString().split("_", 3);
        String uuidString = parts[0];
        if (uuidString.length() > 36) {
            throw new IllegalArgumentException("Invalid packId");
        }
        if (parts.length > 1) {
            this.version = parts[1];
        }
        this.packId = UUID.fromString(uuidString);
        this.chunkIndex = this.getLInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.packId.toString());
        this.putLInt(this.chunkIndex);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}

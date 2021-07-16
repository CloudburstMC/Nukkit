package cn.nukkit.network.protocol;

import lombok.ToString;

import java.util.UUID;

@ToString(exclude = "sha256")
public class ResourcePackDataInfoPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACK_DATA_INFO_PACKET;

    public UUID packId;
    public int maxChunkSize;
    public int chunkCount;
    public long compressedPackSize;
    public byte[] sha256;
    public boolean isPremium;
    public Type type = Type.RESOURCE;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.packId = UUID.fromString(this.getString());
        this.maxChunkSize = this.getLInt();
        this.chunkCount = this.getLInt();
        this.compressedPackSize = this.getLLong();
        this.sha256 = this.getByteArray();
        this.isPremium = this.getBoolean();
        this.type = Type.values()[this.getByte()];
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.packId.toString());
        this.putLInt(this.maxChunkSize);
        this.putLInt(this.chunkCount);
        this.putLLong(this.compressedPackSize);
        this.putByteArray(this.sha256);
        this.putBoolean(this.isPremium);
        this.putByte((byte) this.type.ordinal());
    }

    public static enum Type {

        TYPE_INVALID,
        ADDON,
        CACHED,
        COPY_PROTECTED,
        BEHAVIOR,
        PERSONA_PIECE,
        RESOURCE,
        SKINS,
        WORLD_TEMPLATE,
        COUNT
    }
}

package cn.nukkit.network.protocol;

import lombok.ToString;

import java.util.UUID;

@ToString(exclude = "sha256")
public class ResourcePackDataInfoPacket extends DataPacket {

    public static final byte TYPE_INVALID = 0;
    public static final byte TYPE_ADDON = 1;
    public static final byte TYPE_CACHED = 2;
    public static final byte TYPE_COPY_PROTECTED = 3;
    public static final byte TYPE_BEHAVIOR = 4;
    public static final byte TYPE_PERSONA_PIECE = 5;
    public static final byte TYPE_RESOURCE = 6;
    public static final byte TYPE_SKINS = 7;
    public static final byte TYPE_WORLD_TEMPLATE = 8;
    public static final byte TYPE_COUNT = 9;

    public UUID packId;
    public int maxChunkSize;
    public int chunkCount;
    public long compressedPackSize;
    public byte[] sha256;
    public boolean isPremium = false;
    public byte packType = TYPE_RESOURCE;

    @Override
    public byte pid() {
        return ProtocolInfo.RESOURCE_PACK_DATA_INFO_PACKET;
    }

    @Override
    public void decode() {
        this.packId = UUID.fromString(this.getString());
        this.maxChunkSize = this.getLInt();
        this.chunkCount = this.getLInt();
        this.compressedPackSize = this.getLLong();
        this.sha256 = this.getByteArray();
        this.isPremium = this.getBoolean();
        this.packType = this.getByte();
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
        this.putByte(this.packType);
    }
}

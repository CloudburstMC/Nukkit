package cn.nukkit.network.protocol;

/**
 * Created by CreeperFace on 5.3.2017.
 */
public class ClientboundMapItemDataPacket extends DataPacket {
    public long mapId; // VarLong
    public int unknown; // Unsigned VarInt
    public int unknown2; // Unsigned VarInt
    public int unknown3; // VarLong
    public byte action; // Unsigned byte
    public int unknown4; // Unsigned VarInt
    public int unknown5; // VarInt
    public byte unknown6 = 1; // Byte ~ https://github.com/NiclasOlofsson/MiNET/blob/5064e6d15778f9bfc5dae9fef30eeca10f02f887/src/MiNET/MiNET/Net/Package.cs#L1186
    public byte unknown7 = 0; // Byte
    public boolean showIcons; // Boolean
    public Object icons; // varint<xz>[] TODO: Add icons
    public int direction; // VarInt
    public int x; // varint<xz>
    public int z; // ^^^
    public int columns; // VarInt
    public int rows; // VarInt
    public int offsetX; // varint<xz>
    public int offsetZ; // ^^^
    public byte[] data; // ubyte[]

    public static final int UPDATE = 6;
    public static final int FULL = 4;

    @Override
    public byte pid() {
        return ProtocolInfo.CLIENTBOUND_MAP_ITEM_DATA_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putVarLong(mapId);
        this.putUnsignedVarInt(unknown);
        this.putUnsignedVarInt(unknown2);
        this.putVarLong(unknown3);
        this.putByte(action);
        this.putUnsignedVarInt(unknown4);
        this.putVarInt(unknown5);
        this.putByte(unknown6);
        this.putByte(unknown7);
        this.putBoolean(showIcons);
        this.putUnsignedVarInt(0); // No icons yet
        this.putVarInt(direction);
        this.putVarInt(x);
        this.putVarInt(z);
        this.putVarInt(columns);
        this.putVarInt(rows);
        this.putVarInt(offsetX);
        this.putVarInt(offsetZ);
        this.putByteArray(data);
    }
}

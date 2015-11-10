package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class TileEventPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.TILE_EVENT_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public int x;
    public int y;
    public int z;
    public int case1;
    public int case2;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putInt(this.x);
        this.putInt(this.y);
        this.putInt(this.z);
        this.putInt(this.case1);
        this.putInt(this.case2);
    }
}

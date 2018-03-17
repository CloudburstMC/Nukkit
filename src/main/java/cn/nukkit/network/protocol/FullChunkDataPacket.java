package cn.nukkit.network.protocol;

import cn.nukkit.Player;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FullChunkDataPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.FULL_CHUNK_DATA_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public int chunkX;
    public int chunkZ;
    public byte[] data;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.chunkX);
        this.putVarInt(this.chunkZ);
        this.putByteArray(this.data);
    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}

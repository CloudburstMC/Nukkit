package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.network.CacheEncapsulatedPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BatchPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.BATCH_PACKET;

    public byte[] payload;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.payload = this.get();
    }

    @Override
    public void encode() {

    }

    public void trim() {
        setBuffer(null);
        if (encapsulatedPacket != null) {
            payload = null;
            if (encapsulatedPacket instanceof CacheEncapsulatedPacket && !encapsulatedPacket.hasSplit) {
                CacheEncapsulatedPacket cached = (CacheEncapsulatedPacket) encapsulatedPacket;
                if (cached.internalData != null) cached.buffer = null;
            }
        }
    }

    @Override
    public void handle(Player player) {
        player.server.getNetwork().processBatch(this, player);
    }
}

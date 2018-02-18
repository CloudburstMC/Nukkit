package cn.nukkit.network.protocol;

import cn.nukkit.network.CacheEncapsulatedPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BatchPacket extends DataPacket {

    public byte[] payload;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("BATCH_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {
        this.payload = this.get();
    }

    @Override
    public void encode(PlayerProtocol protocol) {

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

}

package cn.nukkit.network;

import cn.nukkit.raknet.protocol.EncapsulatedPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CacheEncapsulatedPacket extends EncapsulatedPacket {

    public byte[] internalData = null;

    @Override
    public byte[] toBinary() {
        return this.toBinary(false);
    }

    @Override
    public byte[] toBinary(boolean internal) {
        if (this.internalData == null) {
            this.internalData = super.toBinary(internal);
        }
        return this.internalData;
    }
}
package cn.nukkit.network.protocol;

import io.netty.buffer.ByteBuf;

public class OnScreenTextureAnimationPacket extends DataPacket {

    public int effectId;

    @Override
    public short pid() {
        return ProtocolInfo.ON_SCREEN_TEXTURE_ANIMATION_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {
        this.effectId = buffer.readIntLE();
    }

    @Override
    protected void encode(ByteBuf buffer) {
        buffer.writeIntLE(this.effectId);
    }
}

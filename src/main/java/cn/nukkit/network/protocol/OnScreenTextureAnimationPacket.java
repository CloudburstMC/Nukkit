package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class OnScreenTextureAnimationPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ON_SCREEN_TEXTURE_ANIMATION_PACKET;

    public int effectId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putLInt(this.effectId);
    }
}

package cn.nukkit.network.protocol;

public class OnScreenTextureAnimationPacket extends DataPacket {

    public int effectId;

    @Override
    public byte pid() {
        return ProtocolInfo.ON_SCREEN_TEXTURE_ANIMATION_PACKET;
    }

    @Override
    public void decode() {
        this.effectId = this.getLInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putLInt(this.effectId);
    }
}

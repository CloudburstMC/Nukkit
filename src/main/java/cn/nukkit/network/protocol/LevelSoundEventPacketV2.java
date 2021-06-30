package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

@ToString
public class LevelSoundEventPacketV2 extends LevelSoundEventPacket {

    public byte sound;
    public Vextor3f position;
    public int extraData = -1;
    public String entityIdentifier;
    public boolean isBabyMob;
    public boolean isGlobal;

    @Override
    public byte pid() {
        return ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V2;
    }

    @Override
    public void decode() {
        this.sound = this.getByte();
        this.position = this.getVector3f();
        this.extraData = this.getVarInt();
        this.entityIdentifier = this.getString();
        this.isBabyMob = this.getBoolean();
        this.isGlobal = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.soundId);
        this.putVector3f(this.position);
        this.putVarInt(this.extraData);
        this.putString(this.entityIdentifier);
        this.putBoolean(this.isBabyMob);
        this.putBoolean(this.isGlobal);
    }
}

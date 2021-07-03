package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

@ToString
public class LevelSoundEventPacketV1 extends LevelSoundEventPacket {

    public static final byte NETWORK_ID = ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V1;

    public byte sound;
    public Vextor3f position;
    public int extraData;
    public int entityType;
    public boolean isBabyMob;
    public boolean isGlobal;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.sound = this.getByte();
        this.position = this.getVector3f();
        this.extraData = this.getVarInt();
        this.entityIdentifier = this.getVarInt();
        this.isBabyMob = this.getBoolean();
        this.isGlobal = this.getBoolean();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.sound);
        this.putVector3f(this.position);
        this.putVarInt(this.extraData);
        this.putVarInt(this.entityIdentifier);
        this.putBoolean(this.isBabyMob);
        this.putBoolean(this.isGlobal);
    }
}

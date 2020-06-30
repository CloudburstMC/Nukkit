package cn.nukkit.level.particle;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.LevelEventType;
import com.nukkitx.protocol.bedrock.packet.LevelEventPacket;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class MobSpawnParticle extends Particle {

    protected final int width;
    protected final int height;

    public MobSpawnParticle(Vector3f pos, float width, float height) {
        super(pos);
        this.width = (int) width;
        this.height = (int) height;
    }

    @Override
    public BedrockPacket[] encode() {
        LevelEventPacket packet = new LevelEventPacket();
        packet.setType(LevelEventType.PARTICLE_GENERIC_SPAWN);
        packet.setPosition(getPosition());
        packet.setData((this.width & 0xff) + ((this.height & 0xff) << 8));

        return new BedrockPacket[]{packet};
    }
}

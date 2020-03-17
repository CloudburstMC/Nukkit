package cn.nukkit.level.particle;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.data.LevelEventType;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class SmokeParticle extends GenericParticle {

    public SmokeParticle(Vector3i pos) {
        this(Vector3f.from(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f));
    }

    public SmokeParticle(Vector3f pos) {
        this(pos, 0);
    }

    public SmokeParticle(Vector3f pos, int scale) {
        super(pos, LevelEventType.PARTICLE_SMOKE, scale);
    }
}

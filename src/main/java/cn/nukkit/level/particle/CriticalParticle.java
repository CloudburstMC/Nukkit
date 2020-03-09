package cn.nukkit.level.particle;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.LevelEventType;
/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class CriticalParticle extends GenericParticle {
    public CriticalParticle(Vector3f pos) {
        this(pos, 2);
    }

    public CriticalParticle(Vector3f pos, int scale) {
        super(pos, LevelEventType.PARTICLE_CRITICAL, scale);
    }
}

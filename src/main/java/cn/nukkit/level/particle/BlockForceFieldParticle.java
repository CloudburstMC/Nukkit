package cn.nukkit.level.particle;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.LevelEventType;
public class BlockForceFieldParticle extends GenericParticle {
    public BlockForceFieldParticle(Vector3f pos) {
        this(pos, 0);
    }

    public BlockForceFieldParticle(Vector3f pos, int scale) {
        super(pos, LevelEventType.PARTICLE_BLOCK_FORCE_FIELD);
    }
}

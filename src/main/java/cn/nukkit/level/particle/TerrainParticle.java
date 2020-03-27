package cn.nukkit.level.particle;

import cn.nukkit.block.Block;
import cn.nukkit.registry.BlockRegistry;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.LevelEventType;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class TerrainParticle extends GenericParticle {
    public TerrainParticle(Vector3f pos, Block block) {
        super(pos, LevelEventType.PARTICLE_TERRAIN, BlockRegistry.get().getRuntimeId(block.getId(), block.getMeta()));
    }
}

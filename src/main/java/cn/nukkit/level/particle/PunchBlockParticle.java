package cn.nukkit.level.particle;

import cn.nukkit.block.Block;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelEventPacket;

public class PunchBlockParticle extends Particle {

    protected final int data;

    public PunchBlockParticle(Vector3 pos, Block block, BlockFace face) {
        this(pos, block.getId(), block.getDamage(), face);
    }

    public PunchBlockParticle(Vector3 pos, int blockId, int blockDamage, BlockFace face) {
        super(pos.x, pos.y, pos.z);
        this.data = GlobalBlockPalette.getOrCreateRuntimeId(blockId, blockDamage) | (face.getIndex() << 24);
    }

    @Override
    public DataPacket[] encode() {
        LevelEventPacket pk = new LevelEventPacket();
        pk.evid = LevelEventPacket.EVENT_PARTICLE_PUNCH_BLOCK;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.data = this.data;

        return new DataPacket[]{pk};
    }
}

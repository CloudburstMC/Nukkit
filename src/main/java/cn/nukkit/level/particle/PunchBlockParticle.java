package cn.nukkit.level.particle;

import cn.nukkit.block.Block;
import cn.nukkit.math.BlockFace;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.LevelEventType;
import com.nukkitx.protocol.bedrock.packet.LevelEventPacket;

public class PunchBlockParticle extends Particle {

    protected final int data;

    public PunchBlockParticle(Vector3f pos, Block block, BlockFace face) {
        this(pos, block.getId(), block.getMeta(), face);
    }

    public PunchBlockParticle(Vector3f pos, Identifier blockId, int blockDamage, BlockFace face) {
        super(pos);
        this.data = BlockRegistry.get().getRuntimeId(blockId, blockDamage) | (face.getIndex() << 24);
    }

    @Override
    public BedrockPacket[] encode() {
        LevelEventPacket packet = new LevelEventPacket();
        packet.setType(LevelEventType.PARTICLE_CRACK_BLOCK);
        packet.setPosition(this.getPosition());
        packet.setData(this.data);

        return new BedrockPacket[]{packet};
    }
}

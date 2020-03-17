package cn.nukkit.level.particle;

import cn.nukkit.block.Block;
import cn.nukkit.registry.BlockRegistry;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.LevelEventType;
import com.nukkitx.protocol.bedrock.packet.LevelEventPacket;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class DestroyBlockParticle extends Particle {

    protected final int data;

    public DestroyBlockParticle(Vector3f pos, Block block) {
        super(pos);
        this.data = BlockRegistry.get().getRuntimeId(block.getId(), block.getMeta());
    }

    @Override
    public BedrockPacket[] encode() {
        LevelEventPacket packet = new LevelEventPacket();
        packet.setType(LevelEventType.DESTROY);
        packet.setPosition(this.getPosition());
        packet.setData(this.data);

        return new BedrockPacket[]{packet};
    }
}

package cn.nukkit.level.particle;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.LevelEventType;
import com.nukkitx.protocol.bedrock.packet.LevelEventPacket;

/**
 * Created by CreeperFace on 15.4.2017.
 */
public class BoneMealParticle extends Particle {

    private Vector3f position;

    public BoneMealParticle(Vector3i pos) {
        super(pos.toFloat().add(0.5, 0.5, 0.5));
    }

    public BoneMealParticle(Vector3f pos) {
        super(pos);
    }

    @Override
    public BedrockPacket[] encode() {
        LevelEventPacket packet = new LevelEventPacket();
        packet.setType(LevelEventType.BONEMEAL);
        packet.setPosition(this.getPosition());

        return new BedrockPacket[]{packet};
    }
}

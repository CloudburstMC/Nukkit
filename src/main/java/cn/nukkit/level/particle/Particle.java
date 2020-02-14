package cn.nukkit.level.particle;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.BedrockPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Particle {

    private Vector3f position;

    public Particle() {
        this.position = Vector3f.ZERO;
    }

    public Particle(Vector3f position) {
        this.position = position;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    abstract public BedrockPacket[] encode();
}

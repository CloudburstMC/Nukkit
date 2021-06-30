package cn.nukkit.level.particle;

import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelEventPacket;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class ItemBreakParticle extends Particle {

    private final int data;

    public ItemBreakParticle(Vector3 pos, Item item) {
        super(pos.x, pos.y, pos.z);
        this.data = (item.getNetworkId() << 16 | item.getDamage());
    }

    @Override
    public DataPacket[] encode() {
        LevelEventPacket packet = new LevelEventPacket();
        packet.event = (short) (LevelEventPacket.EVENT_ADD_PARTICLE_MASK | Particle.TYPE_ITEM_BREAK);
        packet.position = new Vector3f((float) this.x, (float) this.y, (float) this.z);
        packet.data = this.data;
        return new DataPacket[]{packet};
    }
}

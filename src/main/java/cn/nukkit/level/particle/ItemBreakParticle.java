package cn.nukkit.level.particle;

import cn.nukkit.item.Item;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelEventPacket;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class ItemBreakParticle extends Particle {

    private final int data;

    public ItemBreakParticle(Vector3 pos, Item item) {
        super(pos.x, pos.y, pos.z);
        int networkFullId = RuntimeItems.getRuntimeMapping().getNetworkFullId(item);
        int networkId = RuntimeItems.getNetworkId(networkFullId);
        this.data = (networkId << 16 | item.getDamage());
    }

    @Override
    public DataPacket[] encode() {
        LevelEventPacket packet = new LevelEventPacket();
        packet.evid = (short) (LevelEventPacket.EVENT_ADD_PARTICLE_MASK | Particle.TYPE_ITEM_BREAK);
        packet.x = (float) this.x;
        packet.y = (float) this.y;
        packet.z = (float) this.z;
        packet.data = this.data;
        return new DataPacket[]{packet};
    }
}

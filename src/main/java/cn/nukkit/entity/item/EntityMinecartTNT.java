package cn.nukkit.entity.item;

import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by Snake1999 on 2016/1/30.
 * Package cn.nukkit.entity.item in project Nukkit.
 */
public class EntityMinecartTNT extends EntityMinecartEmpty {

    public static final int NETWORK_ID = 98;

    public EntityMinecartTNT(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public boolean onUpdate(int currentTick) {
        /*Block downSide = this.getLocation().floor().getLevelBlock();
        if (downSide.getId() == Block.ACTIVATOR_RAIL && downSide.isPowered()) { TODO: implement
            explode();
            kill();
        }*/
        return true;
    }

    public void explode() {
        EntityExplosionPrimeEvent event = new EntityExplosionPrimeEvent(this, 4);
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        Explosion explosion = new Explosion(this, event.getForce(), this);
        if (event.isBlockBreaking()) {
            explosion.explodeA();
        }
        explosion.explodeB();
    }

}

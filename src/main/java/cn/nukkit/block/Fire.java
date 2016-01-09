package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.entity.Effect;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityCombustByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Fire extends Flowable {

    public Fire() {
        this(0);
    }

    public Fire(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return FIRE;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public String getName() {
        return "Fire Block";
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (!entity.hasEffect(Effect.FIRE_RESISTANCE)) {
            EntityDamageByBlockEvent ev = new EntityDamageByBlockEvent(this, entity, EntityDamageEvent.CAUSE_FIRE, 1);
            entity.attack(ev);
        }

        EntityCombustByBlockEvent ev = new EntityCombustByBlockEvent(this, entity, 8);
        Server.getInstance().getPluginManager().callEvent(ev);
        if (!ev.isCancelled()) {
            entity.setOnFire(ev.getDuration());
        }
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[0][];
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            for (int s = 0; s <= 5; ++s) {
                Block side = this.getSide(s);
                if (side.getId() != AIR && !(side instanceof Liquid)) {
                    return 0;
                }
            }
            this.getLevel().setBlock(this, new Air(), true);

            return Level.BLOCK_UPDATE_NORMAL;
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (this.getSide(Vector3.SIDE_DOWN).getId() != NETHERRACK) {
                this.getLevel().setBlock(this, new Air(), true);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }
}

package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Effect;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityCombustByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Color;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Lava extends Liquid {

    public Lava() {
        this(0);
    }

    public Lava(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LAVA;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public String getName() {
        return "Lava";
    }

    @Override
    public int tickRate() {
        //todo if getLevel is nether, set to 5
        return 30;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.fallDistance *= 0.5;
        if (!entity.hasEffect(Effect.FIRE_RESISTANCE)) {
            EntityDamageByBlockEvent ev = new EntityDamageByBlockEvent(this, entity, EntityDamageEvent.CAUSE_LAVA, 4);
            entity.attack(ev);
        }

        EntityCombustByBlockEvent ev = new EntityCombustByBlockEvent(this, entity, 15);
        Server.getInstance().getPluginManager().callEvent(ev);
        if (!ev.isCancelled()) {
            entity.setOnFire(ev.getDuration());
        }

        entity.resetFallDistance();
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz) {
        return this.place(item, block, target, face, fx, fy, fz, null);
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        boolean ret = this.getLevel().setBlock(this, this, true, false);
        this.getLevel().scheduleUpdate(this, this.tickRate());

        return ret;
    }

    @Override
    public Color getColor() {
        return Color.lavaColor;
    }

}

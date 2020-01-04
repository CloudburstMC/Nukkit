package cn.nukkit.entity.hostile;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.misc.LightningBolt;
import cn.nukkit.event.entity.CreeperPowerEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.entity.data.EntityFlag.POWERED;

/**
 * @author Box.
 */
public class Creeper extends Mob {

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.7f;
    }

    public Creeper(EntityType<Creeper> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    public boolean isPowered() {
        return getFlag(POWERED);
    }

    public void setPowered(LightningBolt lightningBolt) {
        CreeperPowerEvent ev = new CreeperPowerEvent(this, lightningBolt, CreeperPowerEvent.PowerCause.LIGHTNING);
        this.getServer().getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            this.setFlag(POWERED, true);
            this.namedTag.putBoolean("powered", true);
        }
    }

    public void setPowered(boolean powered) {
        CreeperPowerEvent ev = new CreeperPowerEvent(this, powered ? CreeperPowerEvent.PowerCause.SET_ON : CreeperPowerEvent.PowerCause.SET_OFF);
        this.getServer().getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            this.setFlag(POWERED, powered);
            this.namedTag.putBoolean("powered", powered);
        }
    }

    public void onStruckByLightning(Entity entity) {
        this.setPowered(true);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.getBoolean("powered") || this.namedTag.getBoolean("IsPowered")) {
            this.setFlag(POWERED, true);
        }
        this.setMaxHealth(20);
    }

    @Override
    public String getName() {
        return "Creeper";
    }

    @Override
    public Item[] getDrops() {
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            return new Item[]{Item.get(ItemIds.GUNPOWDER, ThreadLocalRandom.current().nextInt(2) + 1)};
        }
        return new Item[0];
    }
}

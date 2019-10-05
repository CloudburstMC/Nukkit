package cn.nukkit.entity.mob;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.weather.EntityLightningStrike;
import cn.nukkit.event.entity.CreeperPowerEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Box.
 */
public class EntityCreeper extends EntityMob {

    public static final int NETWORK_ID = 33;

    public static final int DATA_SWELL_DIRECTION = 16;
    public static final int DATA_SWELL = 17;
    public static final int DATA_SWELL_OLD = 18;
    public static final int DATA_POWERED = 19;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.7f;
    }

    public EntityCreeper(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public boolean isPowered() {
        return getDataPropertyBoolean(DATA_POWERED);
    }

    public void setPowered(EntityLightningStrike bolt) {
        CreeperPowerEvent ev = new CreeperPowerEvent(this, bolt, CreeperPowerEvent.PowerCause.LIGHTNING);
        this.getServer().getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            this.setDataProperty(new ByteEntityData(DATA_POWERED, 1));
            this.namedTag.putBoolean("powered", true);
        }
    }

    public void setPowered(boolean powered) {
        CreeperPowerEvent ev = new CreeperPowerEvent(this, powered ? CreeperPowerEvent.PowerCause.SET_ON : CreeperPowerEvent.PowerCause.SET_OFF);
        this.getServer().getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            this.setDataProperty(new ByteEntityData(DATA_POWERED, powered ? 1 : 0));
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
            this.dataProperties.putBoolean(DATA_POWERED, true);
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
            return new Item[]{Item.get(Item.GUNPOWDER, ThreadLocalRandom.current().nextInt(2) + 1)};
        }
        return new Item[0];
    }
}

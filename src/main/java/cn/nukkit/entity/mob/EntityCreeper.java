package cn.nukkit.entity.mob;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.weather.EntityLightningStrike;
import cn.nukkit.event.entity.CreeperPowerEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class EntityCreeper extends EntityWalkingMob implements EntityExplosive {

    public static final int NETWORK_ID = 33;

    public EntityCreeper(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

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

    @Override
    public void initEntity() {
        this.setMaxHealth(20);
        super.initEntity();

        if (this.namedTag.contains("powered")) {
            this.setPowered(this.namedTag.getBoolean("powered"));
        }
    }

    public void explode() {
        if (this.closed) return;

        EntityExplosionPrimeEvent ev = new EntityExplosionPrimeEvent(this, this.isPowered() ? 6 : 3);
        this.server.getPluginManager().callEvent(ev);

        if (!ev.isCancelled()) {
            Explosion explosion = new Explosion(this, (float) ev.getForce(), this);

            if (ev.isBlockBreaking() && this.level.getGameRules().getBoolean(GameRule.MOB_GRIEFING)) {
                explosion.explodeA();
            }

            explosion.explodeB();
        }

        this.close();
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();

        for (int i = 0; i < Utils.rand(0, 2); i++) {
            drops.add(Item.get(Item.GUNPOWDER, 0, 1));
        }

        return drops.toArray(new Item[0]);
    }

    @Override
    public int getKillExperience() {
        return 5;
    }

    public boolean isPowered() {
        return this.getDataFlag(DATA_FLAGS, DATA_FLAG_POWERED);
    }

    public void setPowered(boolean charged) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_POWERED, charged);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putBoolean("powered", this.isPowered());
    }

    @Override
    public void onStruckByLightning(Entity lightning) {
        if (this.attack(new EntityDamageByEntityEvent(lightning, this, EntityDamageEvent.DamageCause.LIGHTNING, 5))) {
            if (this.fireTicks < 160) {
                this.setOnFire(8);
            }

            if (lightning instanceof EntityLightningStrike) {
                CreeperPowerEvent event = new CreeperPowerEvent(this, (EntityLightningStrike) lightning, CreeperPowerEvent.PowerCause.LIGHTNING);
                server.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    this.setPowered(true);
                }
            }
        }
    }
}

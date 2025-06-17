package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityPotionEffectEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Utils;

public class EntityPufferfish extends EntityFish {

    public static final int NETWORK_ID = 108;

    private int puffed = 0;

    public EntityPufferfish(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected int getBucketMeta() {
        return 5;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.35f;
    }

    @Override
    public float getHeight() {
        return 0.35f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(3);
        super.initEntity();
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.PUFFERFISH, 0, 1), Item.get(Item.BONE, 0, Utils.rand(0, 2))};
    }

    @Override
    public boolean attack(EntityDamageEvent ev) {
        super.attack(ev);

        if (ev instanceof EntityDamageByEntityEvent && ev.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            Entity damager = ((EntityDamageByEntityEvent) ev).getDamager();
            if (damager instanceof Player) {
                if (this.isPuffed()) return true;
                this.puffed = 200;
                damager.addEffect(Effect.getEffect(Effect.POISON).setDuration(200), EntityPotionEffectEvent.Cause.ATTACK);
                this.setDataProperty(new ByteEntityData(DATA_PUFFERFISH_SIZE, 2));
            }
        }

        return true;
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        if (this.puffed > 0) {
            this.puffed--;

            if (this.puffed <= 0) {
                this.setDataProperty(new ByteEntityData(DATA_PUFFERFISH_SIZE, 0));
            }
        }

        return super.entityBaseTick(tickDiff);
    }

    /**
     * Is puffed
     * @return whether the pufferfish is puffed
     */
    public boolean isPuffed() {
        return this.puffed > 0;
    }
}

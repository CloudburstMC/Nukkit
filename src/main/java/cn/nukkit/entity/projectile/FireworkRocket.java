package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.ItemFireworks;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.SetEntityDataPacket;

import java.util.Random;

/**
 * author: NycuRO
 * NukkitX Project
 */
public class FireworkRocket extends EntityProjectile {

    public static final int NETWORK_ID = 72;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    protected float getGravity() {
        return 0.0f;
    }

    @Override
    protected float getDrag() {
        return 0.01f;
    }

    public ItemFireworks fireworkItem;
    private int lifeTime = 0;

    public FireworkRocket(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
        this.shootingEntity = shootingEntity;
        if (shootingEntity != null) {
            this.setDataProperty(new LongEntityData(DATA_SHOOTER_ID, shootingEntity.getId()));
        }
        Random random = new Random();
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_LINGER, true);
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_GRAVITY, true);
        //$this->getDataPropertyManager()->setItem(16, Item::get($this->fireworks->getId(), $this->fireworks->getDamage(), $this->fireworks->getCount(), $this->fireworks->getCompoundTag()));
        int flyTime = 1;
        try {
            if (this.namedTag.getCompound(ItemFireworks.TAG_FIREWORKS) != null) {
                CompoundTag fireworkNBT = this.namedTag.getCompound(ItemFireworks.TAG_FIREWORKS);
                if (fireworkNBT.getCompound(ItemFireworks.TAG_FLIGHT) != null) {
                    flyTime = fireworkNBT.getByte(ItemFireworks.TAG_FLIGHT);
                }
            }
        } catch (Exception e) {
            this.getServer().getLogger().debug("Error: " + e);
        }
        lifeTime = 20 * flyTime + random.nextInt(5) + random.nextInt(7);
    }

    @Override
    public void sendData(Player[] players, EntityMetadata data) {
        SetEntityDataPacket pk = new SetEntityDataPacket();
        pk.eid = this.getId();
        pk.metadata = data == null ? this.dataProperties : data;
        for (Player player : players) {
            player.dataPacket(pk);
        }
    }

    @Override
    public void spawnTo(Player player) {
        this.setMotion(this.getDirectionVector());
        this.level.addSound(this, Sound.FIREWORK_LAUNCH);
        super.spawnTo(player);
    }

    @Override
    public void despawnFromAll() {
        if (this.namedTag.getCompound(ItemFireworks.TAG_FIREWORKS).getList(ItemFireworks.TAG_EXPLOSIONS) != null) {
            for (Entity entity : this.getLevel().getNearbyEntities(this.boundingBox.expand(5, 5, 5))) {
                if (entity != null) {
                    double distance = this.distance(entity);
                    double k = 22.5;
                    float damage = (float) (k / distance);
                    if (damage > 0) {
                        EntityDamageEvent damageEvent = new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.CUSTOM, damage);
                    }
                }
            }
        }
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.getId();
        pk.data = 0;
        pk.event = EntityEventPacket.FIREWORK_EXPLOSION;
        this.level.addChunkPacket(this.getFloorX() >> 4, this.getFloorZ() >> 4, pk);
        super.despawnFromAll();
        this.level.addSound(new Vector3(this.getFloorX(), this.getFloorY(), this.getFloorZ()), Sound.FIREWORK_BLAST);
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        tickDiff = 1;
        if (this.lifeTime-- < 0) {
            this.kill();
            return true;
        } else {
            super.entityBaseTick(tickDiff);
            return true;
        }
    }
}

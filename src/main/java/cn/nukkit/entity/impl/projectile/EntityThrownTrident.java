package cn.nukkit.entity.impl.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.projectile.ThrownTrident;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.entity.data.EntityFlag.CRITICAL;

/**
 * Created by PetteriM1
 */
public class EntityThrownTrident extends EntityProjectile implements ThrownTrident {

    protected Item trident;
    protected float gravity = 0.04f;
    protected float drag = 0.01f;

    public EntityThrownTrident(EntityType<ThrownTrident> type, Chunk chunk, CompoundTag nbt) {
        this(type, chunk, nbt, null);
    }

    public EntityThrownTrident(EntityType<ThrownTrident> type, Chunk chunk, CompoundTag nbt, BaseEntity shootingEntity) {
        this(type, chunk, nbt, shootingEntity, false);
    }

    public EntityThrownTrident(EntityType<ThrownTrident> type, Chunk chunk, CompoundTag nbt, BaseEntity shootingEntity, boolean critical) {
        super(type, chunk, nbt, shootingEntity);
    }

    private static EntityThrownTrident create(BaseEntity source) {
        Chunk chunk = source.getLevel().getChunk((int) source.x >> 4, (int) source.z >> 4);
        if (chunk == null) throw new IllegalStateException();

        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", source.x + 0.5))
                        .add(new DoubleTag("", source.y))
                        .add(new DoubleTag("", source.z + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", new Random().nextFloat() * 360))
                        .add(new FloatTag("", 0)));

        return new EntityThrownTrident(EntityTypes.THROWN_TRIDENT, chunk, nbt, source);
    }

    @Override
    public float getWidth() {
        return 0.05f;
    }

    @Override
    public float getLength() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.05f;
    }

    @Override
    public float getGravity() {
        return 0.04f;
    }

    @Override
    public float getDrag() {
        return 0.01f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.damage = namedTag.contains("damage") ? namedTag.getDouble("damage") : 8;
        this.trident = namedTag.contains("Trident") ? NBTIO.getItemHelper(namedTag.getCompound("Trident")) : Item.get(0);

        closeOnCollide = false;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.put("Trident", NBTIO.putItemHelper(this.trident));
    }

    @Override
    public Item getTrident() {
        return this.trident != null ? this.trident.clone() : Item.get(0);
    }

    @Override
    public void setTrident(Item item) {
        this.trident = item.clone();
    }

    public void setCritical() {
        this.setCritical(true);
    }

    public boolean isCritical() {
        return this.getFlag(CRITICAL);
    }

    public void setCritical(boolean value) {
        this.setFlag(CRITICAL, value);
    }

    @Override
    public int getResultDamage() {
        int base = super.getResultDamage();

        if (this.isCritical()) {
            base += ThreadLocalRandom.current().nextInt(base / 2 + 2);
        }

        return base;
    }

    @Override
    protected double getBaseDamage() {
        return 8;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        this.timing.startTiming();

        if (this.isCollided && !this.hadCollision) {
            this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ITEM_TRIDENT_HIT_GROUND);
        }

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.onGround || this.hadCollision) {
            this.setCritical(false);
        }

        if (this.age > 1200) {
            this.close();
            hasUpdate = true;
        }

        this.timing.stopTiming();

        return hasUpdate;
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity)));
        float damage = this.getResultDamage();

        EntityDamageEvent ev;
        if (this.shootingEntity == null) {
            ev = new EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage);
        } else {
            ev = new EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, DamageCause.PROJECTILE, damage);
        }
        entity.attack(ev);
        this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ITEM_TRIDENT_HIT);
        this.hadCollision = true;
        this.close();
        EntityThrownTrident newTrident = create(this);
        newTrident.setTrident(this.trident);
        newTrident.spawnToAll();
    }
}

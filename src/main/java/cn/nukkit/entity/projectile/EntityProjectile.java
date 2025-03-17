package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.mob.EntityBlaze;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.ItemArrow;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public abstract class EntityProjectile extends Entity {

    public static final int DATA_SHOOTER_ID = 17;

    /**
     * Arrow will be removed on pickup.
     */
    public static final int PICKUP_NONE_REMOVE = -1;
    /**
     * No player can pick up the arrow.
     */
    public static final int PICKUP_NONE = 0;
    /**
     * All players can pick up the arrow.
     */
    public static final int PICKUP_ANY = 1;
    /**
     * All players on creative mode can pick up the arrow.
     */
    public static final int PICKUP_CREATIVE = 2;

    /**
     * The entity who shot this arrow. May be null.
     */
    public Entity shootingEntity;

    /**
     * The knockback this arrow causes on hit.
     */
    public float knockBack = 0.3f;

    @Getter
    protected int collidedTick;

    protected double getDamage() {
        return namedTag.contains("damage") ? namedTag.getDouble("damage") : getBaseDamage();
    }

    protected double getBaseDamage() {
        return 0;
    }

    public boolean hadCollision = false;

    public int piercing;

    public EntityProjectile(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityProjectile(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt);
        this.shootingEntity = shootingEntity;
        /*if (shootingEntity != null) {
            this.setDataProperty(new LongEntityData(DATA_SHOOTER_ID, shootingEntity.getId()));
        }*/
    }

    /**
     * Get the amount of damage this projectile will deal to the entity it hits.
     * @return damage
     */
    public int getResultDamage() {
        return NukkitMath.ceilDouble(this.getDamage());
    }

    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    public void onCollideWithEntity(Entity entity) {
        this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity)));
        float damage = this instanceof EntitySnowball && entity instanceof EntityBlaze ? 3 : this.getResultDamage();

        EntityDamageEvent ev;
        if (this.shootingEntity == null) {
            ev = new EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage, knockBack);
        } else {
            ev = new EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, DamageCause.PROJECTILE, damage, knockBack);
        }

        if (entity.attack(ev)) {
            this.hadCollision = true;

            this.onHit();

            if (this.fireTicks > 0 && entity.isAlive()) {
                EntityCombustByEntityEvent event = new EntityCombustByEntityEvent(this, entity, 5);
                this.server.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    entity.setOnFire(event.getDuration());
                }
            }

            if (this instanceof EntityArrow && entity instanceof EntityLiving) {
                addEffectFromTippedArrow(entity, ItemArrow.getEffect(((EntityArrow) this).getData()), ev.getFinalDamage());
            }
        }

        this.close();
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(1);
        super.initEntity();
        this.setHealth(1);

        if (this.namedTag.contains("Age")) {
            this.age = this.namedTag.getShort("Age");
        }

        if (this.namedTag.contains("knockback")) {
            this.knockBack = this.namedTag.getFloat("knockback");
        }

        this.updateRotation();
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return (entity instanceof EntityLiving || entity instanceof EntityEndCrystal || entity instanceof EntityMinecartAbstract || entity instanceof EntityBoat || entity instanceof EntityPainting) && !this.onGround && !entity.noClip && !this.noClip;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putShort("Age", this.age);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;
        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }
        this.lastUpdate = currentTick;

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {
            MovingObjectPosition movingObjectPosition = null;

            if (!this.isCollided) {
                if (this.isInsideOfWater()) {
                    this.motionY -= this.getGravity() - (this.getGravity() / 2);
                } else {
                    this.motionY -= this.getGravity();
                }
                this.motionX *= 1 - this.getDrag();
                this.motionZ *= 1 - this.getDrag();
            } else if (this.updateMode % 3 == 1) {
                this.updateMode = 5;

                // Force check for nearby blocks changed
                this.blocksAround = null;
                this.collisionBlocks = null;
                if (!this.level.hasCollisionBlocks(this, this.boundingBox.grow(0.01, 0.01, 0.01))) {
                    this.motionY = -0.00001;
                }
            }

            Vector3 moveVector = new Vector3(this.x + this.motionX, this.y + this.motionY, this.z + this.motionZ);

            Entity[] list = this.noClip ? new Entity[0] : this.getLevel().getCollidingEntities(this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1, 1, 1), this);

            double nearDistance = Integer.MAX_VALUE;
            Entity nearEntity = null;

            for (Entity entity : list) {
                if (/*!entity.canCollideWith(this) || */(entity == this.shootingEntity && this.age < 5) || (entity instanceof Player && ((Player) entity).getGamemode() == Player.SPECTATOR)) {
                    continue;
                }

                AxisAlignedBB axisalignedbb = entity.boundingBox.grow(0.3, 0.3, 0.3);
                MovingObjectPosition ob = axisalignedbb.calculateIntercept(this, moveVector);

                if (ob == null) {
                    continue;
                }

                double distance = this.distanceSquared(ob.hitVector);

                if (distance < nearDistance) {
                    nearDistance = distance;
                    nearEntity = entity;
                }
            }

            if (nearEntity != null) {
                movingObjectPosition = MovingObjectPosition.fromEntity(nearEntity);
            }

            if (movingObjectPosition != null) {
                if (movingObjectPosition.entityHit != null) {
                    onCollideWithEntity(movingObjectPosition.entityHit);
                    return true;
                }
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            if (this.isCollided && !this.hadCollision) { // Collide with block
                // Make sure last move tick is broadcast
                // However previous yaw & pitch are actually the correct ones
                if (!(this instanceof EntityFishingHook)) this.addMovement(this.x, this.y, this.z, this.lastYaw, this.lastPitch, this.lastYaw);

                this.hadCollision = true;

                this.motionX = 0;
                this.motionY = 0;
                this.motionZ = 0;

                this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromBlock(this.getFloorX(), this.getFloorY(), this.getFloorZ(), -1, this)));
                this.onHit();
                this.onHitGround(moveVector);

                if (this instanceof EntityArrow || this instanceof EntityThrownTrident) {
                    this.updateMode = 5; // The projectile is collided with a block so only update it if the block changes or to check age for despawn
                }
                return false;
            } else if (!this.isCollided && this.hadCollision) {
                this.hadCollision = false;
            }

            if (!this.hadCollision || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001) {
                updateRotation();
                hasUpdate = true;
            }

            this.updateMovement();
        }

        return hasUpdate;
    }

    /**
     * Update projectile rotation to match its motion.
     */
    public void updateRotation() {
        double f = Math.sqrt((this.motionX * this.motionX) + (this.motionZ * this.motionZ));
        this.yaw = Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI;
        this.pitch = Math.atan2(this.motionY, f) * 180 / Math.PI;
    }

    /**
     * Add inaccuracy to projectile movement. Used internally with dispensers.
     *
     * @param modifier multiplier
     */
    public void inaccurate(float modifier) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        this.motionX += rand.nextGaussian() * 0.007499999832361937 * modifier;
        this.motionY += rand.nextGaussian() * 0.007499999832361937 * modifier;
        this.motionZ += rand.nextGaussian() * 0.007499999832361937 * modifier;
    }

    protected void onHit() {

    }

    protected void onHitGround(Vector3 moveVector) {
        this.collidedTick = level.getServer().getTick();
        Block block = level.getBlock(this.chunk, moveVector.getFloorX(), moveVector.getFloorY(), moveVector.getFloorZ(), false);
        block.onEntityCollide(this);
    }
}

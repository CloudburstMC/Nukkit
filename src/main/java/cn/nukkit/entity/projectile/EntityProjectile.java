package cn.nukkit.entity.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityProjectile extends Entity {

    public static final int DATA_SHOOTER_ID = 17;

    public Entity shootingEntity = null;

    protected double getDamage() {
        return namedTag.contains("damage") ? namedTag.getDouble("damage") : 2;
    }

    public boolean hadCollision = false;

    protected double damage = 0;

    public EntityProjectile(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityProjectile(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt);
        this.shootingEntity = shootingEntity;
        if (shootingEntity != null) {
            this.setDataProperty(new LongEntityData(DATA_SHOOTER_ID, shootingEntity.getId()));
        }
    }

    public int getResultDamage() {
        return NukkitMath.ceilDouble(Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ) * getDamage());
    }

    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    public void onCollideWithEntity(Entity entity) {
        this.server.getPluginManager().callEvent(new ProjectileHitEvent(this));
        float damage = this.getResultDamage();

        EntityDamageEvent ev;
        if (this.shootingEntity == null) {
            ev = new EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage);
        } else {
            ev = new EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, DamageCause.PROJECTILE, damage);
        }
        entity.attack(ev);
        this.hadCollision = true;

        if (this.fireTicks > 0) {
            EntityCombustByEntityEvent event = new EntityCombustByEntityEvent(this, entity, 5);
            this.server.getPluginManager().callEvent(ev);
            if (!event.isCancelled()) {
                entity.setOnFire(event.getDuration());
            }
        }
        this.close();
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.setMaxHealth(1);
        this.setHealth(1);
        if (this.namedTag.contains("Age")) {
            this.age = this.namedTag.getShort("Age");
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return entity instanceof EntityLiving && !this.onGround;
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
                this.motionY -= this.getGravity();
            }

            Vector3 moveVector = new Vector3(this.x + this.motionX, this.y + this.motionY, this.z + this.motionZ);

            Entity[] list = this.getLevel().getCollidingEntities(this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1, 1, 1), this);

            double nearDistance = Integer.MAX_VALUE;
            Entity nearEntity = null;

            for (Entity entity : list) {
                if (/*!entity.canCollideWith(this) or */
                        (entity == this.shootingEntity && this.ticksLived < 5)
                        ) {
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

            if (this.isCollided && !this.hadCollision) { //collide with block
                this.hadCollision = true;

                this.motionX = 0;
                this.motionY = 0;
                this.motionZ = 0;

                this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromBlock(this.getFloorX(), this.getFloorY(), this.getFloorZ(), -1, this)));
                return false;
            } else if (!this.isCollided && this.hadCollision) {
                this.hadCollision = false;
            }

            if (!this.hadCollision || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001) {
                double f = Math.sqrt((this.motionX * this.motionX) + (this.motionZ * this.motionZ));
                this.yaw = Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI;
                this.pitch = Math.atan2(this.motionY, f) * 180 / Math.PI;
                hasUpdate = true;
            }

            this.updateMovement();

        }

        return hasUpdate;
    }
}
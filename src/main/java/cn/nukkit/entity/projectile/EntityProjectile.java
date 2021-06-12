package cn.nukkit.entity.projectile;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.entity.item.EntityEndCrystal;
import cn.nukkit.event.block.BellRingEvent;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EntityProjectile extends Entity {

    public static final int DATA_SHOOTER_ID = 17;

    public Entity shootingEntity = null;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean hasAge = true;
    
    protected double getDamage() {
        return namedTag.contains("damage") ? namedTag.getDouble("damage") : getBaseDamage();
    }

    protected double getBaseDamage() {
        return 0;
    }

    public boolean hadCollision = false;

    public boolean closeOnCollide = true;

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

    @PowerNukkitOnly("Allows to modify the damage based on the entity being damaged")
    @Since("1.4.0.0-PN")
    public int getResultDamage(@Nullable Entity entity) {
        return getResultDamage();
    }

    public int getResultDamage() {
        return NukkitMath.ceilDouble(Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ) * getDamage());
    }

    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    public void onCollideWithEntity(Entity entity) {
        ProjectileHitEvent projectileHitEvent = new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity));
        this.server.getPluginManager().callEvent(projectileHitEvent);

        if (projectileHitEvent.isCancelled()) {
            return;
        }

        float damage = this.getResultDamage(entity);

        EntityDamageEvent ev;
        if (this.shootingEntity == null) {
            ev = new EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage);
        } else {
            ev = new EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, DamageCause.PROJECTILE, damage);
        }
        if (entity.attack(ev)) {
            addHitEffect();
            this.hadCollision = true;

            if (this.fireTicks > 0) {
                EntityCombustByEntityEvent event = new EntityCombustByEntityEvent(this, entity, 5);
                this.server.getPluginManager().callEvent(ev);
                if (!event.isCancelled()) {
                    entity.setOnFire(event.getDuration());
                }
            }
        }
        afterCollisionWithEntity(entity);
        if (closeOnCollide) {
            this.close();
        }
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected void afterCollisionWithEntity(Entity entity) {
        
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.setMaxHealth(1);
        this.setHealth(1);
        if (this.namedTag.contains("Age") && this.hasAge) {
            this.age = this.namedTag.getShort("Age");
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return (entity instanceof EntityLiving || entity instanceof EntityEndCrystal) && !this.onGround;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        if (this.hasAge) {
            this.namedTag.putShort("Age", this.age);
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected void updateMotion() {
        this.motionY -= this.getGravity();
        this.motionX *= 1 - this.getDrag();
        this.motionZ *= 1 - this.getDrag();
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
                updateMotion();
            }

            Vector3 moveVector = new Vector3(this.x + this.motionX, this.y + this.motionY, this.z + this.motionZ);

            Entity[] list = this.getLevel().getCollidingEntities(this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1, 1, 1), this);

            double nearDistance = Integer.MAX_VALUE;
            Entity nearEntity = null;

            for (Entity entity : list) {
                if (/*!entity.canCollideWith(this) or */
                        (entity == this.shootingEntity && this.ticksLived < 5) ||
                                (entity instanceof Player && ((Player) entity).getGamemode() == Player.SPECTATOR)
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
                    hasUpdate = true;
                    if (closed) {
                        return true;
                    }
                }
            }

            Position position = getPosition();
            Vector3 motion = getMotion();
            this.move(this.motionX, this.motionY, this.motionZ);

            if (this.isCollided && !this.hadCollision) { //collide with block
                this.hadCollision = true;

                this.motionX = 0;
                this.motionY = 0;
                this.motionZ = 0;

                this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromBlock(this.getFloorX(), this.getFloorY(), this.getFloorZ(), -1, this)));
                onCollideWithBlock(position, motion);
                addHitEffect();
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

    public void updateRotation() {
        double f = Math.sqrt((this.motionX * this.motionX) + (this.motionZ * this.motionZ));
        this.yaw = Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI;
        this.pitch = Math.atan2(this.motionY, f) * 180 / Math.PI;
    }

    public void inaccurate(float modifier) {
        Random rand = ThreadLocalRandom.current();

        this.motionX += rand.nextGaussian() * 0.007499999832361937 * modifier;
        this.motionY += rand.nextGaussian() * 0.007499999832361937 * modifier;
        this.motionZ += rand.nextGaussian() * 0.007499999832361937 * modifier;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected void onCollideWithBlock(Position position, Vector3 motion) {
        for (Block collisionBlock : level.getCollisionBlocks(getBoundingBox().grow(0.1, 0.1, 0.1))) {
            onCollideWithBlock(position, motion, collisionBlock);
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected boolean onCollideWithBlock(Position position, Vector3 motion, Block collisionBlock) {
        return collisionBlock.onProjectileHit(this, position, motion);
    }

    @PowerNukkitOnly
    protected void addHitEffect() {

    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean hasAge() {
        return hasAge;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setAge(boolean hasAge) {
        this.hasAge = hasAge;
    }
}

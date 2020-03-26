package cn.nukkit.entity.impl.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.impl.EntityLiving;
import cn.nukkit.entity.misc.EnderCrystal;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.level.Location;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import java.util.Set;

import static com.nukkitx.protocol.bedrock.data.EntityFlag.CRITICAL;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityProjectile extends BaseEntity {

    protected float damage;
    public boolean hadCollision = false;
    public boolean closeOnCollide = true;

    public EntityProjectile(EntityType<?> type, Location location) {
        super(type, location);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForNumber("damage", v -> this.damage = v.floatValue());
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.floatTag("damage", this.damage);
    }

    public int getResultDamage() {
        return NukkitMath.ceilFloat(this.motion.length() * getDamage());
    }

    public float getDamage() {
        return damage <= 0 ? getBaseDamage() : damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    protected float getBaseDamage() {
        return 0;
    }

    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    public void onCollideWithEntity(Entity entity) {
        this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity)));
        float damage = this.getResultDamage();

        EntityDamageEvent ev;
        if (this.getOwner() == null) {
            ev = new EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage);
        } else {
            ev = new EntityDamageByChildEntityEvent(this.getOwner(), this, entity, DamageCause.PROJECTILE, damage);
        }
        if(entity.attack(ev)){
            this.hadCollision = true;

            if (this.fireTicks > 0) {
                EntityCombustByEntityEvent event = new EntityCombustByEntityEvent(this, entity, 5);
                this.server.getPluginManager().callEvent(ev);
                if (!event.isCancelled()) {
                    entity.setOnFire(event.getDuration());
                }
            }
        }
        if (closeOnCollide) {
            this.close();
        }
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.setMaxHealth(1);
        this.setHealth(1);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return (entity instanceof EntityLiving || entity instanceof EnderCrystal) && !this.onGround;
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
                this.motion = motion.sub(0, this.getGravity(), 0);
            }

            Vector3f moveVector = this.position.add(this.motion);

            Set<Entity> collidingEntities = this.getLevel().getCollidingEntities(
                    this.boundingBox.addCoord(this.motion).expand(1, 1, 1),
                    this);

            double nearDistance = Integer.MAX_VALUE;
            Entity nearEntity = null;

            for (Entity entity : collidingEntities) {
                if (/*!entity.canCollideWith(this) or */
                        (entity == this.getOwner() && this.ticksLived < 5)
                ) {
                    continue;
                }

                AxisAlignedBB axisalignedbb = entity.getBoundingBox().grow(0.3f, 0.3f, 0.3f);
                MovingObjectPosition ob = axisalignedbb.calculateIntercept(this.getPosition(), moveVector);

                if (ob == null) {
                    continue;
                }

                double distance = this.position.distanceSquared(ob.hitVector);

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

            this.move(this.motion);

            if (this.isCollided && !this.hadCollision) { //collide with block
                this.hadCollision = true;

                this.motion = Vector3f.ZERO;

                this.server.getPluginManager().callEvent(new ProjectileHitEvent(this,
                        MovingObjectPosition.fromBlock(this.position.toInt(), -1, this.getPosition())));
                return false;
            } else if (!this.isCollided && this.hadCollision) {
                this.hadCollision = false;
            }

            if (!this.hadCollision || motion.length() > 0.00001) {
                double f = Math.sqrt((this.motion.getX() * this.motion.getX()) + (this.motion.getZ() * this.motion.getZ()));
                this.yaw = (float) (Math.atan2(this.motion.getX(), this.motion.getZ()) * 180 / Math.PI);
                this.pitch = (float) (Math.atan2(this.motion.getY(), f) * 180 / Math.PI);
                hasUpdate = true;
            }

            this.updateMovement();

        }

        return hasUpdate;
    }

    public void setCritical() {
        this.setCritical(true);
    }

    public boolean isCritical() {
        return this.data.getFlag(CRITICAL);
    }

    public void setCritical(boolean value) {
        this.data.setFlag(CRITICAL, value);
    }
}

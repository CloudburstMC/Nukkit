package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.vehicle.VehicleDamageEvent;
import cn.nukkit.event.vehicle.VehicleDestroyEvent;
import cn.nukkit.event.vehicle.VehicleMoveEvent;
import cn.nukkit.event.vehicle.VehicleUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBoat;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.SmokeParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by yescallop on 2016/2/13.
 */
public class EntityBoat extends EntityVehicle {

    public static final int NETWORK_ID = 90;

    public static final int DATA_WOOD_ID = 20;

    public EntityBoat(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.dataProperties.putByte(DATA_WOOD_ID, this.namedTag.getByte("woodID"));

        this.setHealth(4);
        this.setMaxHealth(4);
    }

    @Override
    public float getHeight() {
        return 0.7f;
    }

    @Override
    public float getWidth() {
        return 1.6f;
    }

    @Override
    protected float getDrag() {
        return 0.1f;
    }

    @Override
    protected float getGravity() {
        return 0.1f;
    }

    @Override
    public float getBaseOffset() {
        return 0.35F;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.BOAT;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (invulnerable) {
            return false;
        } else {
            // Event start
            VehicleDamageEvent event = new VehicleDamageEvent(this, source.getEntity(), source.getFinalDamage());
            getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }
            // Event stop
            performHurtAnimation((int) event.getDamage());

            boolean instantKill = false;

            if (source instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
                instantKill = damager instanceof Player && ((Player) damager).isCreative();
            }

            if (instantKill || getDamage() > 40) {
                // Event start
                VehicleDestroyEvent event2 = new VehicleDestroyEvent(this, source.getEntity());
                getServer().getPluginManager().callEvent(event2);
                if (event2.isCancelled()) {
                    return false;
                }
                // Event stop
                if (linkedEntity != null) {
                    mountEntity(linkedEntity);
                }

                if (instantKill && (!hasCustomName())) {
                    kill();
                } else {
                    if (level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                        this.level.dropItem(this, new ItemBoat());
                    }
                    close();
                }
            }
        }

        return true;
    }

    @Override
    public void close() {
        super.close();

        if (this.linkedEntity instanceof Player) {
            this.linkedEntity.riding = null;
        }

        SmokeParticle particle = new SmokeParticle(this);
        this.level.addParticle(particle);
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
            super.onUpdate(currentTick);

            this.motionY = (this.level.getBlock(new Vector3(this.x, this.y, this.z)).getBoundingBox() != null || this.isInsideOfWater()) ? getGravity() : -0.08;

            if (this.checkObstruction(this.x, this.y, this.z)) {
                hasUpdate = true;
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            double friction = 1 - this.getDrag();

            if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
                friction *= this.getLevel().getBlock(this.temporalVector.setComponents((int) Math.floor(this.x), (int) Math.floor(this.y - 1), (int) Math.floor(this.z) - 1)).getFrictionFactor();
            }

            this.motionX *= friction;
            this.motionY *= 1 - this.getDrag();
            this.motionZ *= friction;

            if (this.onGround) {
                this.motionY *= -0.5;
            }

            Location from = new Location(lastX, lastY, lastZ, lastYaw, lastPitch, level);
            Location to = new Location(this.x, this.y, this.z, this.yaw, this.pitch, level);

            this.getServer().getPluginManager().callEvent(new VehicleUpdateEvent(this));

            if (!from.equals(to)) {
                this.getServer().getPluginManager().callEvent(new VehicleMoveEvent(this, from, to));
            }

            this.updateMovement();
        }

        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    @Override
    public boolean onInteract(Player player, Item item) {
        if (this.linkedEntity != null) {
            return false;
        }

        super.mountEntity(player);
        return true;
    }
}
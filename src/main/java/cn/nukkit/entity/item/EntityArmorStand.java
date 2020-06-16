package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.entity.passive.EntityWaterAnimal;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.vehicle.VehicleMoveEvent;
import cn.nukkit.event.vehicle.VehicleUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBoat;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.SmokeParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;

import java.util.ArrayList;

/**
 * Created by yescallop on 2016/2/13.
 */
public class EntityArmorStand extends Entity {

    public static final int NETWORK_ID = 61;

    private boolean small;

    public EntityArmorStand(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.setMaxHealth(20);
        this.setHealth(20);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
    }

    @Override
    public float getHeight() {
        return small ? 0.9875f : 1.975f;
    }

    @Override
    public float getWidth() {
        return small ? 0.25f : 0.5f;
    }

    @Override
    protected float getDrag() {
        return 0.1f;
    }

    @Override
    protected float getGravity() {
        return 0.03999999910593033F;
    }

    @Override
    public float getBaseOffset() {
        return 0.375F;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (invulnerable) {
            return false;
        } else {
            if (isAlive()) {
                close();
            }
            return true;
        }
    }

    @Override
    public void close() {
        super.close();
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

            if (this.checkObstruction(this.x, this.y, this.z)) {
                hasUpdate = true;
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            double friction = 1 - this.getDrag();

            if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
                friction *= this.getLevel().getBlock(this.temporalVector.setComponents((int) Math.floor(this.x), (int) Math.floor(this.y - 1), (int) Math.floor(this.z) - 1)).getFrictionFactor();
            }

            this.motionX *= friction;

            this.motionZ *= friction;

            this.updateMovement();
        }

        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (this.passengers.size() >= 2) {
            return false;
        }

        return super.onInteract(player, item, clickedPos);
    }

    @Override
    public void applyEntityCollision(Entity entity) {
        if (this.riding == null && entity.riding != this && !entity.passengers.contains(this)) {
            if (!entity.boundingBox.intersectsWith(this.boundingBox.grow(0.20000000298023224, -0.1, 0.20000000298023224))) {
                return;
            }

            double diffX = entity.x - this.x;
            double diffZ = entity.z - this.z;

            double direction = NukkitMath.getDirection(diffX, diffZ);

            if (direction >= 0.009999999776482582D) {
                direction = Math.sqrt(direction);
                diffX /= direction;
                diffZ /= direction;

                double d3 = Math.min(1 / direction, 1);

                diffX *= d3;
                diffZ *= d3;
                diffX *= 0.05000000074505806;
                diffZ *= 0.05000000074505806;
                diffX *= 1 + entityCollisionReduction;

                if (this.riding == null) {
                    motionX -= diffX;
                    motionZ -= diffZ;
                }
            }
        }
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public void kill() {
        super.kill();
    }
}

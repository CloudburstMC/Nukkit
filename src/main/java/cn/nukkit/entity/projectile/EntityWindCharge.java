package cn.nukkit.entity.projectile;

import cn.nukkit.block.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class EntityWindCharge extends EntityProjectile {

    public static final int NETWORK_ID = 143;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.3125f;
    }

    @Override
    public float getLength() {
        return 0.3125f;
    }

    @Override
    public float getHeight() {
        return 0.3125f;
    }

    @Override
    protected float getGravity() {
        return 0.0f;
    }

    @Override
    protected float getDrag() {
        return 0.01f;
    }

    public EntityWindCharge(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityWindCharge(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (this.age > 1200 || this.isCollided || this.hadCollision) {
            this.close();
            return false;
        }

        super.onUpdate(currentTick);
        return !this.closed;
    }

    private static final double BURST_RADIUS = 3.0;
    private static final double BURST_POWER = 1.1;

    @Override
    public void onHit() {
        this.level.addParticle(new GenericParticle(this, Particle.TYPE_WIND_EXPLOSION));
        this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_WIND_CHARGE_BURST);

        double explosionSize = BURST_RADIUS * 2d;
        double radiusSquared = BURST_RADIUS * BURST_RADIUS;

        double minX = NukkitMath.floorDouble(this.x - explosionSize - 1);
        double maxX = NukkitMath.ceilDouble(this.x + explosionSize + 1);
        double minY = NukkitMath.floorDouble(this.y - explosionSize - 1);
        double maxY = NukkitMath.ceilDouble(this.y + explosionSize + 1);
        double minZ = NukkitMath.floorDouble(this.z - explosionSize - 1);
        double maxZ = NukkitMath.ceilDouble(this.z + explosionSize + 1);

        AxisAlignedBB box = new SimpleAxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);

        for (Entity entity : level.getNearbyEntities(box, this)) {
            if (entity == this) {
                continue;
            }

            double distanceSquared = entity.distanceSquared(this);

            if (distanceSquared > 0 && distanceSquared < radiusSquared) {
                double impact = (1.0 - (Math.sqrt(distanceSquared) / BURST_RADIUS)) * BURST_POWER;
                Vector3 direction = entity.subtract(this.add(0, -0.1, 0)).normalize();
                entity.setMotion(entity.getMotion().add(direction.multiply(impact)));
            }
        }

        for (Block block : level.getCollisionBlocks(this, box, false)) {
            if (block instanceof BlockButton || block instanceof BlockDoor || block instanceof BlockTrapdoor || block instanceof BlockLever) {
                if (block.distanceSquared(this) < radiusSquared) {
                    block.onActivate(null, null);
                }
            }
        }
    }
}

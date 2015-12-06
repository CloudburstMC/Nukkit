package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.CriticalParticle;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

import cn.nukkit.utils.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Arrow extends Projectile {
    public static final int NETWORK_ID = 80;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public float width = 0.5f;
    public float length = 0.5f;
    public float height = 0.5f;

    protected float gravity = 0.05f;
    protected float drag = 0.01f;

    protected double damage = 2;

    protected boolean isCritical;

    public Arrow(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public Arrow(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        this(chunk, nbt, shootingEntity, false);
    }

    public Arrow(FullChunk chunk, CompoundTag nbt, Entity shootingEntity, boolean critical) {
        super(chunk, nbt, shootingEntity);
        this.isCritical = critical;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        boolean hasUpdate = super.onUpdate(currentTick);

        if (!this.hadCollision && this.isCritical) {
            Random random = new Random();
            this.level.addParticle(new CriticalParticle(this.add(
                    this.width / 2 + ((double) NukkitMath.randomRange(random, -100, 100)) / 500,
                    this.height / 2 + ((double) NukkitMath.randomRange(random, -100, 100)) / 500,
                    this.width / 2 + ((double) NukkitMath.randomRange(random, -100, 100)) / 500)));
        } else if (this.onGround) {
            this.isCritical = false;
        }

        if (this.age > 1200) {
            this.kill();
            hasUpdate = true;
        }

        return hasUpdate;
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.type = Arrow.NETWORK_ID;
        pk.eid = this.getId();
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = (float) this.motionX;
        pk.speedY = (float) this.motionY;
        pk.speedZ = (float) this.motionZ;
        pk.metadata = this.dataProperties;
        player.dataPacket(pk);

        super.spawnTo(player);
    }
}

package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityBlaze;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.DataPacket;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntitySnowball extends EntityProjectile {

    public static final int NETWORK_ID = 81;
    private static final byte[] particleCounts = new byte[24];
    private static int particleIndex = 0;
    static {
        for (int i = 0; i < particleCounts.length; i++) {
            particleCounts[i] = (byte)(ThreadLocalRandom.current().nextInt(10) + 5);
        }
    }

    private static int nextParticleCount() {
        int index = particleIndex++;
        if (index >= particleCounts.length) {
            particleIndex = index = 0;
        }
        return particleCounts[index];
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getLength() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    protected float getGravity() {
        return 0.03f;
    }

    @Override
    protected float getDrag() {
        return 0.01f;
    }

    public EntitySnowball(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntitySnowball(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        this.timing.startTiming();

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.age > 1200 || this.isCollided) {
            this.kill();
            hasUpdate = true;
        }

        this.timing.stopTiming();

        return hasUpdate;
    }

    @PowerNukkitOnly
    @Override
    public int getResultDamage(@Nullable Entity entity) {
        return entity instanceof EntityBlaze ? 3 : super.getResultDamage(entity);
    }

    @Override
    protected void addHitEffect() {
        int particles = nextParticleCount();
        DataPacket[] particlePackets = new GenericParticle(this, Particle.TYPE_SNOWBALL_POOF).encode();
        int length = particlePackets.length;
        DataPacket[] allPackets = Arrays.copyOf(particlePackets, length * particles);
        for (int i = length; i < allPackets.length; i++) {
            allPackets[i] = particlePackets[i % length];
        }
        int chunkX = (int) x >> 4;
        int chunkZ = (int) z >> 4;
        Level level = this.level;
        level.getServer().batchPackets(level.getChunkPlayers(chunkX, chunkZ).values().toArray(Player.EMPTY_ARRAY), allPackets);
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Snowball";
    }
}

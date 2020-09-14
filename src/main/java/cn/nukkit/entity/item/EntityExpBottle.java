package cn.nukkit.entity.item;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.EnchantParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.level.particle.SpellParticle;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author xtypr
 */
public class EntityExpBottle extends EntityProjectile {

    public static final int NETWORK_ID = 68;

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
        return 0.1f;
    }

    @Override
    protected float getDrag() {
        return 0.01f;
    }

    public EntityExpBottle(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityExpBottle(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        this.timing.startTiming();

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.age > 1200) {
            this.kill();
            hasUpdate = true;
        }

        if (this.isCollided) {
            this.kill();
            this.dropXp();
            hasUpdate = true;
        }

        this.timing.stopTiming();

        return hasUpdate;
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        this.kill();
        this.dropXp();
    }

    public void dropXp() {
        Particle particle1 = new EnchantParticle(this);
        this.getLevel().addParticle(particle1);
        Particle particle2 = new SpellParticle(this, 0x00385dc6);
        this.getLevel().addParticle(particle2);

        this.getLevel().dropExpOrb(this, ThreadLocalRandom.current().nextInt(3, 12));
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    protected void addHitEffect() {
        this.getLevel().addSound(this, Sound.RANDOM_GLASS);
    }
}

package cn.nukkit.entity.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.particle.EnchantParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.level.particle.SpellParticle;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author xtypr
 */
public class XpBottle extends Projectile {

    public XpBottle(EntityType<XpBottle> type, Chunk chunk, CompoundTag nbt) {
        this(type, chunk, nbt, null);
    }

    public XpBottle(EntityType<XpBottle> type, Chunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(type, chunk, nbt, shootingEntity);
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
}

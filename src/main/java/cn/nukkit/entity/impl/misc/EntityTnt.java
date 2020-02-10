package cn.nukkit.entity.impl.misc;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.misc.Tnt;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.gamerule.GameRules;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import static cn.nukkit.entity.data.EntityData.FUSE_LENGTH;
import static cn.nukkit.entity.data.EntityFlag.IGNITED;

/**
 * @author MagicDroidX
 */
public class EntityTnt extends BaseEntity implements Tnt, EntityExplosive {

    protected int fuse;

    public EntityTnt(EntityType<Tnt> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.98f;
    }

    @Override
    public float getLength() {
        return 0.98f;
    }

    @Override
    public float getHeight() {
        return 0.98f;
    }

    @Override
    public float getGravity() {
        return 0.04f;
    }

    @Override
    public float getDrag() {
        return 0.02f;
    }

    @Override
    protected float getBaseOffset() {
        return 0.49f;
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    protected void initEntity() {
        super.initEntity();

        if (namedTag.contains("Fuse")) {
            fuse = namedTag.getByte("Fuse");
        } else {
            fuse = 80;
        }

        this.setFlag(IGNITED, true);
        this.setIntData(FUSE_LENGTH, fuse);

        this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_FIZZ);
    }


    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public void saveNBT() {
        super.saveNBT();
        namedTag.putByte("Fuse", fuse);
    }

    public boolean onUpdate(int currentTick) {

        if (closed) {
            return false;
        }

        this.timing.startTiming();

        int tickDiff = currentTick - lastUpdate;

        if (tickDiff <= 0 && !justCreated) {
            return true;
        }

        if (fuse <= 5 || fuse % 5 == 0) {
            this.setIntData(FUSE_LENGTH, fuse);

            updateData();
        }

        lastUpdate = currentTick;

        boolean hasUpdate = entityBaseTick(tickDiff);

        if (isAlive()) {

            motionY -= getGravity();

            move(motionX, motionY, motionZ);

            float friction = 1 - getDrag();

            motionX *= friction;
            motionY *= friction;
            motionZ *= friction;

            updateMovement();

            if (onGround) {
                motionY *= -0.5;
                motionX *= 0.7;
                motionZ *= 0.7;
            }

            fuse -= tickDiff;

            if (fuse <= 0) {
                if (this.level.getGameRules().get(GameRules.TNT_EXPLODES))
                    explode();
                kill();
            }

        }

        this.timing.stopTiming();

        return hasUpdate || fuse >= 0 || Math.abs(motionX) > 0.00001 || Math.abs(motionY) > 0.00001 || Math.abs(motionZ) > 0.00001;
    }

    public void explode() {
        EntityExplosionPrimeEvent event = new EntityExplosionPrimeEvent(this, 4);
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        Explosion explosion = new Explosion(this, event.getForce(), this);
        if (event.isBlockBreaking()) {
            explosion.explodeA();
        }
        explosion.explodeB();
    }

    @Override
    public int getFuse() {
        return fuse;
    }

    @Override
    public void setFuse(int fuse) {
        this.fuse = fuse;
    }
}

package cn.nukkit.entity.impl.misc;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.misc.PrimedTnt;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.Location;
import cn.nukkit.level.gamerule.GameRules;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.protocol.bedrock.data.SoundEvent;

import static com.nukkitx.protocol.bedrock.data.entity.EntityData.FUSE_LENGTH;
import static com.nukkitx.protocol.bedrock.data.entity.EntityFlag.IGNITED;

/**
 * @author MagicDroidX
 */
public class EntityPrimedTnt extends BaseEntity implements PrimedTnt, EntityExplosive {

    protected int fuse = 80;

    public EntityPrimedTnt(EntityType<PrimedTnt> type, Location location) {
        super(type, location);
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

        this.data.setFlag(IGNITED, true);
        this.data.setInt(FUSE_LENGTH, fuse);

        this.getLevel().addLevelSoundEvent(this.getPosition(), SoundEvent.FIZZ);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForInt("Fuse", this::setFuse);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.intTag("Fuse", this.getFuse());
    }

    public boolean canCollideWith(Entity entity) {
        return false;
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
            this.data.setInt(FUSE_LENGTH, fuse);

            this.data.update();
        }

        lastUpdate = currentTick;

        boolean hasUpdate = entityBaseTick(tickDiff);

        if (isAlive()) {

            this.motion = this.motion.sub(0, this.getGravity(), 0);

            move(this.motion);

            float friction = 1 - getDrag();

            this.motion = this.motion.mul(friction);

            updateMovement();

            if (onGround) {
                this.motion = this.motion.mul(-0.5, 0.7, 0.7);
            }

            fuse -= tickDiff;

            if (fuse <= 0) {
                if (this.level.getGameRules().get(GameRules.TNT_EXPLODES))
                    explode();
                kill();
            }

        }

        this.timing.stopTiming();

        return hasUpdate || fuse >= 0 || this.motion.length() > 0.00001;
    }

    public void explode() {
        EntityExplosionPrimeEvent event = new EntityExplosionPrimeEvent(this, 4);
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        Explosion explosion = new Explosion(this.getLevel(), this.getPosition(), event.getForce(), this);
        if (event.isBlockBreaking()) {
            explosion.explodeA();
        }
        explosion.explodeB();
    }

    @Override
    public int getFuse() {
        return this.data.getInt(FUSE_LENGTH);
    }

    @Override
    public void setFuse(int fuse) {
        this.data.setInt(FUSE_LENGTH, fuse);
    }
}

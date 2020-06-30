package cn.nukkit.entity.impl.misc;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.misc.ExperienceOrb;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.level.Location;
import cn.nukkit.player.Player;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import static com.nukkitx.protocol.bedrock.data.entity.EntityData.EXPERIENCE_VALUE;

/**
 * Created on 2015/12/26 by xtypr.
 * Package cn.nukkit.entity in project Nukkit .
 */
public class EntityExperienceOrb extends BaseEntity implements ExperienceOrb {

    public Player closestPlayer = null;
    private int age;
    private int pickupDelay;

    public EntityExperienceOrb(EntityType<ExperienceOrb> type, Location location) {
        super(type, location);
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
    public float getGravity() {
        return 0.04f;
    }

    @Override
    public float getDrag() {
        return 0.02f;
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        setMaxHealth(5);
        setHealth(5);

        this.data.setInt(EXPERIENCE_VALUE, 1);

        //call event item spawn event
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForInt("experience value", this::setExperience);
        tag.listenForShort("Age", v -> this.age = v);
        tag.listenForShort("PickupDelay", v -> this.pickupDelay = v);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.intTag("experience value", this.getExperience());
        tag.shortTag("Age", (short) this.age);
        tag.shortTag("PickupDelay", (short) this.pickupDelay);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return (source.getCause() == DamageCause.VOID ||
                source.getCause() == DamageCause.FIRE_TICK ||
                (source.getCause() == DamageCause.ENTITY_EXPLOSION ||
                        source.getCause() == DamageCause.BLOCK_EXPLOSION) &&
                        !this.isInsideOfWater()) && super.attack(source);
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

        boolean hasUpdate = entityBaseTick(tickDiff);
        if (this.isAlive()) {
            if (this.pickupDelay > 0 && this.pickupDelay < 32767) { //Infinite delay
                this.pickupDelay -= tickDiff;
                if (this.pickupDelay < 0) {
                    this.pickupDelay = 0;
                }
            } else {
                for (Entity entity : this.level.getCollidingEntities(this.boundingBox, this)) {
                    if (entity instanceof Player) {
                        if (((Player) entity).pickupEntity(this, false)) {
                            return true;
                        }
                    }
                }
            }

            this.motion = this.motion.sub(0, this.getGravity(), 0);

            if (this.checkObstruction(this.getPosition())) {
                hasUpdate = true;
            }

            if (this.closestPlayer == null || this.closestPlayer.getPosition().distanceSquared(this.getPosition()) > 64.0D) {
                for (Player p : level.getPlayers().values()) {
                    if (!p.isSpectator() && p.getPosition().distance(this.getPosition()) <= 8) {
                        this.closestPlayer = p;
                        break;
                    }
                }
            }

            if (this.closestPlayer != null && this.closestPlayer.isSpectator()) {
                this.closestPlayer = null;
            }

            if (this.closestPlayer != null) {
                Vector3f diffPos = this.closestPlayer.getPosition().add(0, this.closestPlayer.getEyeHeight() / 2, 0).sub(this.getPosition()).div(8);
                double d = diffPos.length();
                double diff = 1.0D - d;

                if (diff > 0.0D) {
                    diff = diff * diff;
                    this.motion = this.motion.add(diffPos.div(d * diff * 0.1));
                }
            }

            this.move(this.motion);

            double friction = 1d - this.getDrag();

            if (this.onGround && (Math.abs(this.motion.getX()) > 0.00001 || Math.abs(this.motion.getZ()) > 0.00001)) {
                friction = this.getLevel().getBlock(this.getPosition().add(0, -1, -1)).getFrictionFactor() * friction;
            }

            this.motion = this.motion.mul(friction, 1 - this.getDrag(), friction);

            if (this.onGround) {
                this.motion = this.motion.mul(1, -0.5, 1);
            }

            this.updateMovement();

            if (this.age > 6000) {
                this.kill();
                hasUpdate = true;
            }

        }

        return hasUpdate || !this.onGround || this.motion.abs().length() > 0.00001;
    }

    public int getExperience() {
        return this.data.getInt(EXPERIENCE_VALUE);
    }

    public void setExperience(int experience) {
        if (experience <= 0) {
            throw new IllegalArgumentException("XP amount must be greater than 0, got " + experience);
        }
        this.data.setInt(EXPERIENCE_VALUE, experience);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public int getPickupDelay() {
        return pickupDelay;
    }

    @Override
    public void setPickupDelay(int pickupDelay) {
        this.pickupDelay = pickupDelay;
    }
}

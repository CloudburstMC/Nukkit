package cn.nukkit.entity.impl.misc;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.impl.EntityLiving;
import cn.nukkit.entity.misc.AreaEffectCloud;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.level.Location;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.InstantEffect;
import cn.nukkit.potion.Potion;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.nukkitx.protocol.bedrock.data.EntityData.*;
import static com.nukkitx.protocol.bedrock.data.EntityFlag.FIRE_IMMUNE;
import static com.nukkitx.protocol.bedrock.data.EntityFlag.NO_AI;

public class EntityAreaEffectCloud extends BaseEntity implements AreaEffectCloud {
    private static final String TAG_DURATION = "Duration";
    private static final String TAG_REAPPLICATION_DELAY = "ReapplicationDelay";
    private static final String TAG_DURATION_ON_USE = "DurationOnUse";
    private static final String TAG_RADIUS_ON_USE = "RadiusOnUse";
    private static final String TAG_RADIUS_PER_TICK = "RadiusPerTick";
    private static final String TAG_OWNER_ID = "OwnerID";
    private static final String TAG_POTION_ID = "PotionId";
    private static final String TAG_RADIUS = "Radius";
    private static final String TAG_PARTICLE_ID = "ParticleId";
    private static final String TAG_MOB_EFFECTS = "mobEffects";
    private static final String TAG_PARTICLE_COLOR = "ParticleColor";
    private static final String TAG_SPAWN_TICK = "SpawnTick";
    private static final String TAG_RADIUS_CHANGE_ON_PICKUP = "RadiusChangeOnPickup";
    private static final String TAG_INITIAL_RADIUS = "InitialRadius";
    private static final String TAG_PICKUP_COUNT = "PickupCount";

    protected int reapplicationDelay;
    protected int durationOnUse;
    protected float initialRadius;
    protected float radiusOnUse;
    protected int nextApply;
    protected List<Effect> cloudEffects;
    protected int particleColor;
    protected boolean particleColorSet;
    private int lastAge;

    public EntityAreaEffectCloud(EntityType<?> type, Location location) {
        super(type, location);
    }

    @Override
    public int getWaitTime() {
        return this.data.getInt(AREA_EFFECT_CLOUD_WAITING);
    }

    @Override
    public void setWaitTime(int waitTime) {
        this.data.setInt(AREA_EFFECT_CLOUD_WAITING, waitTime);
    }

    @Override
    public short getPotionId() {
        return this.data.getShort(POTION_AUX_VALUE);
    }

    @Override
    public void setPotionId(int potionId) {
        this.data.setShort(POTION_AUX_VALUE, potionId & 0xFFFF);
        this.recalculatePotionColor();
    }

    @Override
    public void recalculatePotionColor() {
        int a;
        int r;
        int g;
        int b;

        int color;
        if (this.particleColorSet) {
            color = this.particleColor;
            a = (color & 0xFF000000) >> 24;
            r = (color & 0x00FF0000) >> 16;
            g = (color & 0x0000FF00) >> 8;
            b = color & 0x000000FF;
        } else {
            a = 255;
            Effect effect = Potion.getEffect(getPotionId(), true);
            if (effect == null) {
                r = 40;
                g = 40;
                b = 255;
            } else {
                int[] colors = effect.getColor();
                r = colors[0];
                g = colors[1];
                b = colors[2];
            }
        }

        setPotionColor(a, r, g, b);
    }

    @Override
    public int getPotionColor() {
        return this.data.getInt(POTION_COLOR);
    }

    @Override
    public void setPotionColor(int argp) {
        this.data.setInt(POTION_COLOR, argp);
    }

    @Override
    public void setPotionColor(int alpha, int red, int green, int blue) {
        setPotionColor(((alpha & 0xff) << 24) | ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff));
    }

    @Override
    public int getPickupCount() {
        return this.data.getInt(AREA_EFFECT_CLOUD_PICKUP_COUNT);
    }

    @Override
    public void setPickupCount(int pickupCount) {
        this.data.setInt(AREA_EFFECT_CLOUD_PICKUP_COUNT, pickupCount);
    }

    @Override
    public float getRadiusChangeOnPickup() {
        return this.data.getFloat(AREA_EFFECT_CLOUD_RADIUS_CHANGE_ON_PICKUP);
    }

    @Override
    public void setRadiusChangeOnPickup(float radiusChangeOnPickup) {
        this.data.setFloat(AREA_EFFECT_CLOUD_RADIUS_CHANGE_ON_PICKUP, radiusChangeOnPickup);
    }

    @Override
    public float getRadiusPerTick() {
        return this.data.getFloat(AREA_EFFECT_CLOUD_RADIUS_PER_TICK);
    }

    @Override
    public void setRadiusPerTick(float radiusPerTick) {
        this.data.setFloat(AREA_EFFECT_CLOUD_RADIUS_PER_TICK, radiusPerTick);
    }

    @Override
    public long getSpawnTime() {
        return this.data.getLong(AREA_EFFECT_CLOUD_SPAWN_TIME);
    }

    @Override
    public void setSpawnTime(long spawnTime) {
        this.data.setLong(AREA_EFFECT_CLOUD_SPAWN_TIME, spawnTime);
    }

    @Override
    public int getDuration() {
        return this.data.getInt(AREA_EFFECT_CLOUD_DURATION);
    }

    @Override
    public void setDuration(int duration) {
        this.data.setInt(AREA_EFFECT_CLOUD_DURATION, duration);
    }

    @Override
    public float getRadius() {
        return this.data.getFloat(AREA_EFFECT_CLOUD_RADIUS);
    }

    @Override
    public void setRadius(float radius) {
        this.data.setFloat(AREA_EFFECT_CLOUD_RADIUS, radius);
    }

    @Override
    public int getParticleId() {
        return this.data.getInt(AREA_EFFECT_CLOUD_PARTICLE_ID);
    }

    @Override
    public void setParticleId(int particleId) {
        this.data.setInt(AREA_EFFECT_CLOUD_PARTICLE_ID, particleId);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.invulnerable = true;
        this.data.setFlag(FIRE_IMMUNE, true);
        this.data.setFlag(NO_AI, true);
        this.data.setShort(AREA_EFFECT_CLOUD_PARTICLE_ID, 32);
        this.data.setLong(AREA_EFFECT_CLOUD_SPAWN_TIME, this.level.getCurrentTick());
        this.data.setInt(AREA_EFFECT_CLOUD_PICKUP_COUNT, 0);
        this.setDuration(600);
        this.initialRadius = 3f;
        this.setRadius(this.initialRadius);
        this.setRadiusChangeOnPickup(-0.5F);
        this.setRadiusPerTick(-0.005F);
        this.setWaitTime(10);
        this.setMaxHealth(1);
        this.setHealth(1);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForList(TAG_MOB_EFFECTS, CompoundTag.class, effectTags -> {
            for (CompoundTag effectTag : effectTags) {
                this.cloudEffects.add(Effect.getEffect(effectTag));
            }
        });

        tag.listenForShort(TAG_POTION_ID, this::setPotionId);
        tag.listenForInt(TAG_DURATION, this::setDuration);
        tag.listenForInt(TAG_DURATION_ON_USE, v -> this.durationOnUse = v);
        tag.listenForInt(TAG_REAPPLICATION_DELAY, v -> this.reapplicationDelay = v);
        tag.listenForFloat(TAG_INITIAL_RADIUS, v -> this.initialRadius = v);
        tag.listenForFloat(TAG_RADIUS, this::setRadius);
        tag.listenForFloat(TAG_RADIUS_CHANGE_ON_PICKUP, this::setRadiusChangeOnPickup);
        tag.listenForFloat(TAG_RADIUS_ON_USE, v -> this.radiusOnUse = v);
        tag.listenForFloat(TAG_RADIUS_PER_TICK, this::setRadiusPerTick);
        tag.listenForInt("WaitTime", this::setWaitTime);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        List<CompoundTag> effects = new ArrayList<>();
        for (Effect effect : this.cloudEffects) {
            effects.add(effect.createTag());
        }
        tag.listTag(TAG_MOB_EFFECTS, CompoundTag.class, effects);
        tag.intTag(TAG_PARTICLE_COLOR, getPotionColor());
        tag.shortTag(TAG_POTION_ID, getPotionId());
        tag.intTag(TAG_DURATION, getDuration());
        tag.intTag(TAG_DURATION_ON_USE, durationOnUse);
        tag.intTag(TAG_REAPPLICATION_DELAY, reapplicationDelay);
        tag.floatTag(TAG_RADIUS, getRadius());
        tag.floatTag(TAG_RADIUS_CHANGE_ON_PICKUP, getRadiusChangeOnPickup());
        tag.floatTag(TAG_RADIUS_ON_USE, radiusOnUse);
        tag.floatTag(TAG_RADIUS_PER_TICK, getRadiusPerTick());
        tag.intTag("WaitTime", getWaitTime());
        tag.floatTag(TAG_INITIAL_RADIUS, initialRadius);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        this.timing.startTiming();

        super.onUpdate(currentTick);

        int age = this.age;
        float radius = getRadius();
        int waitTime = getWaitTime();
        if (age < waitTime) {
            radius = initialRadius;
        } else if (age > waitTime + getDuration()) {
            kill();
        } else {
            int tickDiff = age - lastAge;
            radius += getRadiusPerTick() * tickDiff;
            if ((nextApply -= tickDiff) <= 0) {
                nextApply = reapplicationDelay + 10;

                Set<Entity> collidingEntities = level.getCollidingEntities(getBoundingBox(), this);
                if (!collidingEntities.isEmpty()) {
                    radius += radiusOnUse;
                    radiusOnUse /= 2;

                    setDuration(getDuration() + durationOnUse);

                    for (Entity collidingEntity : collidingEntities) {
                        if (collidingEntity == this || !(collidingEntity instanceof EntityLiving)) {
                            continue;
                        }

                        for (Effect effect : cloudEffects) {
                            if (effect instanceof InstantEffect) {
                                boolean damage = false;
                                if (effect.getId() == Effect.HARMING) {
                                    damage = true;
                                }
                                if (collidingEntity.isUndead()) {
                                    damage = !damage; // invert effect if undead
                                }

                                if (damage) {
                                    collidingEntity.attack(new EntityDamageByEntityEvent(this, collidingEntity, EntityDamageEvent.DamageCause.MAGIC, (float) (0.5 * (double) (6 << (effect.getAmplifier() + 1)))));
                                } else {
                                    collidingEntity.heal(new EntityRegainHealthEvent(collidingEntity, (float) (0.5 * (double) (4 << (effect.getAmplifier() + 1))), EntityRegainHealthEvent.CAUSE_MAGIC));
                                }

                                continue;
                            }

                            collidingEntity.addEffect(effect);
                        }
                    }
                }
            }
        }

        this.lastAge = age;

        if (radius <= 1.5 && age >= waitTime) {
            setRadius(radius);
            kill();
        } else {
            setRadius(radius);
        }

        float height = getHeight();
        boundingBox.setBounds(getX() - radius, getY() - height, getZ() - radius,
                getX() + radius, getY() + height, getZ() + radius);
        this.data.setFloat(BOUNDING_BOX_HEIGHT, height);
        this.data.setFloat(BOUNDING_BOX_WIDTH, radius);

        this.timing.stopTiming();

        return true;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return entity instanceof EntityLiving;
    }

    @Override
    public float getHeight() {
        return 0.3F + (getRadius() / 2F);
    }

    @Override
    public float getWidth() {
        return getRadius();
    }

    @Override
    public float getLength() {
        return getRadius();
    }

    @Override
    public float getGravity() {
        return 0;
    }

    @Override
    public float getDrag() {
        return 0;
    }

    @Override
    public List<Effect> getCloudEffects() {
        return cloudEffects;
    }
}

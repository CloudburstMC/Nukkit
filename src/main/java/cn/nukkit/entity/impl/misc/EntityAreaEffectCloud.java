package cn.nukkit.entity.impl.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.impl.EntityLiving;
import cn.nukkit.entity.misc.AreaEffectCloud;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.InstantEffect;
import cn.nukkit.potion.Potion;

import static cn.nukkit.entity.data.EntityData.*;
import static cn.nukkit.entity.data.EntityFlag.*;

public class EntityAreaEffectCloud extends BaseEntity implements AreaEffectCloud {
    protected int reapplicationDelay;
    protected int durationOnUse;
    protected float initialRadius;
    protected float radiusOnUse;
    protected int nextApply;
    protected List<Effect> cloudEffects;
    private int lastAge;
    
    public EntityAreaEffectCloud(EntityType<?> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }
    
    @Override
    public int getWaitTime() {
        return this.getIntData(AREA_EFFECT_CLOUD_WAITING);
    }
    
    @Override
    public void setWaitTime(int waitTime) {
        this.setIntData(AREA_EFFECT_CLOUD_WAITING, waitTime);
    }
    
    @Override
    public int getPotionId() {
        return this.getShortData(POTION_AUX_VALUE);
    }
    
    @Override
    public void setPotionId(int potionId) {
        this.setShortData(POTION_AUX_VALUE, potionId & 0xFFFF);
    }
    
    @Override
    public void recalculatePotionColor() {
        int a;
        int r;
        int g;
        int b;
    
        int color;
        if (namedTag.contains("ParticleColor")) {
            color = namedTag.getInt("ParticleColor");
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
        return this.getIntData(POTION_COLOR);
    }
    
    @Override
    public void setPotionColor(int alpha, int red, int green, int blue) {
        setPotionColor(((alpha & 0xff) << 24) | ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff));
    }
    
    @Override
    public void setPotionColor(int argp) {
        this.setIntData(POTION_COLOR, argp);
    }
    
    @Override
    public int getPickupCount() {
        return this.getIntData(AREA_EFFECT_CLOUD_PICKUP_COUNT);
    }
    
    @Override
    public void setPickupCount(int pickupCount) {
        this.setIntData(AREA_EFFECT_CLOUD_PICKUP_COUNT, pickupCount);
    } 
    
    @Override
    public float getRadiusChangeOnPickup() {
        return this.getFloatData(AREA_EFFECT_CLOUD_RADIUS_CHANGE_ON_PICKUP);
    }
    
    @Override
    public void setRadiusChangeOnPickup(float radiusChangeOnPickup) {
        this.setFloatData(AREA_EFFECT_CLOUD_RADIUS_CHANGE_ON_PICKUP, radiusChangeOnPickup);
    }
    
    @Override
    public float getRadiusPerTick() {
        return this.getFloatData(AREA_EFFECT_CLOUD_RADIUS_PER_TICK);
    }
    
    @Override
    public void setRadiusPerTick(float radiusPerTick) {
        this.setFloatData(AREA_EFFECT_CLOUD_RADIUS_PER_TICK, radiusPerTick);
    }
    
    @Override
    public long getSpawnTime() {
        return this.getLongData(AREA_EFFECT_CLOUD_SPAWN_TIME);
    }
    
    @Override
    public void setSpawnTime(long spawnTime) {
        this.setLongData(AREA_EFFECT_CLOUD_SPAWN_TIME, spawnTime);
    }
    
    @Override
    public int getDuration() {
        return this.getIntData(AREA_EFFECT_CLOUD_DURATION);
    }
    
    @Override
    public void setDuration(int duration) {
        this.setIntData(AREA_EFFECT_CLOUD_DURATION, duration);
    }
    
    @Override
    public float getRadius() {
        return this.getFloatData(AREA_EFFECT_CLOUD_RADIUS);
    }
    
    @Override
    public void setRadius(float radius) {
        this.setFloatData(AREA_EFFECT_CLOUD_RADIUS, radius);
    }
    
    @Override
    public int getParticleId() {
        return this.getIntData(AREA_EFFECT_CLOUD_PARTICLE_ID);
    }
    
    @Override
    public void setParticleId(int particleId) {
        this.setIntData(AREA_EFFECT_CLOUD_PARTICLE_ID, particleId);
    }
    
    @Override
    protected void initEntity() {
        super.initEntity();
        this.invulnerable = true;
        this.setFlag(FIRE_IMMUNE, true);
        this.setFlag(IMMOBILE, true);
        this.setShortData(AREA_EFFECT_CLOUD_PARTICLE_ID, 32);
        this.setLongData(AREA_EFFECT_CLOUD_SPAWN_TIME, this.level.getCurrentTick());
        this.setIntData(AREA_EFFECT_CLOUD_PICKUP_COUNT, 0);
        
        cloudEffects = new ArrayList<>(1);
        for (CompoundTag effectTag : namedTag.getList("mobEffects", CompoundTag.class).getAll()) {
            Effect effect = Effect.getEffect(effectTag.getByte("Id"))
                    .setAmbient(effectTag.getBoolean("Ambient"))
                    .setAmplifier(effectTag.getByte("Amplifier"))
                    .setVisible(effectTag.getBoolean("DisplayOnScreenTextureAnimation"))
                    .setDuration(effectTag.getInt("Duration"));
            cloudEffects.add(effect);
        }
        int displayedPotionId = namedTag.getShort("PotionId");
        setPotionId(displayedPotionId);
        recalculatePotionColor();
        
        if (namedTag.contains("Duration")) {
            setDuration(namedTag.getInt("Duration"));
        } else {
            setDuration(600);
        }
        if (namedTag.contains("DurationOnUse")) {
            durationOnUse = namedTag.getInt("DurationOnUse");
        } else {
            durationOnUse = 0;
        }
        if (namedTag.contains("ReapplicationDelay")) {
            reapplicationDelay = namedTag.getInt("ReapplicationDelay");
        } else {
            reapplicationDelay = 0;
        }
        if (namedTag.contains("InitialRadius")) {
            initialRadius = namedTag.getFloat("InitialRadius");
        } else {
            initialRadius = 3.0F;
        }
        if (namedTag.contains("Radius")) {
            setRadius(namedTag.getFloat("Radius"));
        } else {
            setRadius(initialRadius);
        }
        if (namedTag.contains("RadiusChangeOnPickup")) {
            setRadiusChangeOnPickup(namedTag.getFloat("RadiusChangeOnPickup"));
        } else {
            setRadiusChangeOnPickup(-0.5F);
        }
        if (namedTag.contains("RadiusOnUse")) {
            radiusOnUse = namedTag.getFloat("RadiusOnUse");
        } else {
            radiusOnUse = -0.5F;
        }
        if (namedTag.contains("RadiusPerTick")) {
            setRadiusPerTick(namedTag.getFloat("RadiusPerTick"));
        } else {
            setRadiusPerTick(-0.005F);
        }
        if (namedTag.contains("WaitTime")) {
            setWaitTime(namedTag.getInt("WaitTime"));
        } else {
            setWaitTime(10);
        }
    
        setMaxHealth(1);
        setHealth(1);
    }
    
    @Override
    public boolean attack(EntityDamageEvent source) {
        return false;
    }
    
    @Override
    public void saveNBT() {
        super.saveNBT();
        ListTag<CompoundTag> effectsTag = new ListTag<>("mobEffects");
        for (Effect effect : cloudEffects) {
            effectsTag.add(new CompoundTag().putByte("Id", effect.getId())
                    .putBoolean("Ambient", effect.isAmbient())
                    .putByte("Amplifier", effect.getAmplifier())
                    .putBoolean("DisplayOnScreenTextureAnimation", effect.isVisible())
                    .putInt("Duration", effect.getDuration())
            );
        }
        //TODO Do we really need to save the entity data to nbt or is it already saved somewhere?
        namedTag.putList(effectsTag);
        namedTag.putInt("ParticleColor", getPotionColor());
        namedTag.putShort("PotionId", getPotionId());
        namedTag.putInt("Duration", getDuration());
        namedTag.putInt("DurationOnUse", durationOnUse);
        namedTag.putInt("ReapplicationDelay", reapplicationDelay);
        namedTag.putFloat("Radius", getRadius());
        namedTag.putFloat("RadiusChangeOnPickup", getRadiusChangeOnPickup());
        namedTag.putFloat("RadiusOnUse", radiusOnUse);
        namedTag.putFloat("RadiusPerTick", getRadiusPerTick());
        namedTag.putInt("WaitTime", getWaitTime());
        namedTag.putFloat("InitialRadius", initialRadius);
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
                                if (effect.getId() == Effect.HARMING){
                                    damage = true;
                                }
                                if (collidingEntity.isUndead()){
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
        boundingBox.setBounds(x - radius, y - height, z - radius, x + radius, y + height, z + radius);
        this.setFloatData(BOUNDING_BOX_HEIGHT, height);
        this.setFloatData(BOUNDING_BOX_WIDTH, radius);
        
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

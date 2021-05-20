package cn.nukkit.entity.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.InstantEffect;
import cn.nukkit.potion.Potion;

import java.util.ArrayList;
import java.util.List;

public class EntityAreaEffectCloud extends Entity {
    public static final int NETWORK_ID = 95;
    
    protected int reapplicationDelay;
    protected int durationOnUse;
    protected float initialRadius;
    protected float radiusOnUse;
    protected int nextApply;
    public List<Effect> cloudEffects;
    private int lastAge;
    
    public EntityAreaEffectCloud(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
    
    public int getWaitTime() {
        return this.getDataPropertyInt(DATA_AREA_EFFECT_CLOUD_WAITING);
    }
    
    public void setWaitTime(int waitTime) {
        setWaitTime(waitTime, true);
    }
    
    public void setWaitTime(int waitTime, boolean send) {
        this.setDataProperty(new IntEntityData(DATA_AREA_EFFECT_CLOUD_WAITING, waitTime), send);
    }
    
    public int getPotionId() {
        return this.getDataPropertyShort(DATA_POTION_AUX_VALUE);
    }
    
    public void setPotionId(int potionId) {
        setPotionId(potionId, true);
    }
    
    public void setPotionId(int potionId, boolean send) {
        this.setDataProperty(new ShortEntityData(DATA_POTION_AUX_VALUE, potionId & 0xFFFF), send);
    }
    
    public void recalculatePotionColor() {
        recalculatePotionColor(true);
    }
    
    public void recalculatePotionColor(boolean send) {
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
    
        setPotionColor(a, r, g, b, send);
    }
    
    public int getPotionColor() {
        return this.getDataPropertyInt(DATA_POTION_COLOR);
    }
    
    public void setPotionColor(int alpha, int red, int green, int blue, boolean send) {
        setPotionColor(((alpha & 0xff) << 24) | ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff), send);
    }
    
    public void setPotionColor(int argp) {
        setPotionColor(argp, true);
    }
    
    public void setPotionColor(int argp, boolean send) {
        this.setDataProperty(new IntEntityData(DATA_POTION_COLOR, argp), send);
    }
    
    public int getPickupCount() {
        return this.getDataPropertyInt(DATA_PICKUP_COUNT);
    }
    
    public void setPickupCount(int pickupCount) {
        setPickupCount(pickupCount, true);
    }
    
    public void setPickupCount(int pickupCount, boolean send) {
        this.setDataProperty(new IntEntityData(DATA_PICKUP_COUNT, pickupCount), send);
    } 
    
    public float getRadiusChangeOnPickup() {
        return this.getDataPropertyFloat(DATA_CHANGE_ON_PICKUP);
    }
    
    public void setRadiusChangeOnPickup(float radiusChangeOnPickup) {
        setRadiusChangeOnPickup(radiusChangeOnPickup, true);
    }
    
    public void setRadiusChangeOnPickup(float radiusChangeOnPickup, boolean send) {
        this.setDataProperty(new FloatEntityData(DATA_CHANGE_ON_PICKUP, radiusChangeOnPickup), send);
    }
    
    public float getRadiusPerTick() {
        return this.getDataPropertyFloat(DATA_CHANGE_RATE);
    }
    
    public void setRadiusPerTick(float radiusPerTick) {
        setRadiusPerTick(radiusPerTick, true);
    }
    
    public void setRadiusPerTick(float radiusPerTick, boolean send) {
        this.setDataProperty(new FloatEntityData(DATA_CHANGE_RATE, radiusPerTick), send);
    }
    
    public long getSpawnTime() {
        return this.getDataPropertyInt(DATA_SPAWN_TIME);
    }
    
    public void setSpawnTime(long spawnTime) {
        setSpawnTime(spawnTime, true);
    }
    
    public void setSpawnTime(long spawnTime, boolean send) {
        this.setDataProperty(new LongEntityData(DATA_SPAWN_TIME, spawnTime), send);
    }
    
    public int getDuration() {
        return this.getDataPropertyInt(DATA_DURATION);
    }
    
    public void setDuration(int duration) {
        setDuration(duration, true);
    }
    
    public void setDuration(int duration, boolean send) {
        this.setDataProperty(new IntEntityData(DATA_DURATION, duration), send);
    }
    
    public float getRadius() {
        return this.getDataPropertyFloat(DATA_AREA_EFFECT_CLOUD_RADIUS);
    }
    
    public void setRadius(float radius) {
        setRadius(radius, true);
    }
    
    public void setRadius(float radius, boolean send) {
        this.setDataProperty(new FloatEntityData(DATA_AREA_EFFECT_CLOUD_RADIUS, radius), send);
    }
    
    public int getParticleId() {
        return this.getDataPropertyInt(DATA_AREA_EFFECT_CLOUD_PARTICLE_ID);
    }
    
    public void setParticleId(int particleId) {
        setParticleId(particleId, true);
    }
    
    public void setParticleId(int particleId, boolean send) {
        this.setDataProperty(new IntEntityData(DATA_AREA_EFFECT_CLOUD_PARTICLE_ID, particleId), send);
    }
    
    @Override
    protected void initEntity() {
        super.initEntity();
        this.invulnerable = true;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_FIRE_IMMUNE, true);
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_IMMOBILE, true);
        this.setDataProperty(new ShortEntityData(DATA_AREA_EFFECT_CLOUD_PARTICLE_ID, 32), false);
        this.setDataProperty(new LongEntityData(DATA_SPAWN_TIME, this.level.getCurrentTick()), false);
        this.setDataProperty(new IntEntityData(DATA_PICKUP_COUNT, 0), false);
        
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
        setPotionId(displayedPotionId, false);
        recalculatePotionColor();
        
        if (namedTag.contains("Duration")) {
            setDuration(namedTag.getInt("Duration"), false);
        } else {
            setDuration(600, false);
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
            setRadius(namedTag.getFloat("Radius"), false);
        } else {
            setRadius(initialRadius, false);
        }
        if (namedTag.contains("RadiusChangeOnPickup")) {
            setRadiusChangeOnPickup(namedTag.getFloat("RadiusChangeOnPickup"), false);
        } else {
            setRadiusChangeOnPickup(-0.5F, false);
        }
        if (namedTag.contains("RadiusOnUse")) {
            radiusOnUse = namedTag.getFloat("RadiusOnUse");
        } else {
            radiusOnUse = -0.5F;
        }
        if (namedTag.contains("RadiusPerTick")) {
            setRadiusPerTick(namedTag.getFloat("RadiusPerTick"), false);
        } else {
            setRadiusPerTick(-0.005F, false);
        }
        if (namedTag.contains("WaitTime")) {
            setWaitTime(namedTag.getInt("WaitTime"), false);
        } else {
            setWaitTime(10, false);
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
        
        boolean sendRadius = age % 10 == 0;
    
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

                Entity[] collidingEntities = level.getCollidingEntities(getBoundingBox());
                if (collidingEntities.length > 0) {
                    radius += radiusOnUse;
                    radiusOnUse /= 2;

                    setDuration(getDuration() + durationOnUse);

                    for (Entity collidingEntity : collidingEntities) {
                        if (collidingEntity == this || !(collidingEntity instanceof EntityLiving)) continue;

                        for (Effect effect : cloudEffects) {
                            if (effect instanceof InstantEffect) {
                                boolean damage = false;
                                if (effect.getId() == Effect.HARMING) damage = true;
                                if (collidingEntity.isUndead()) damage = !damage; // invert effect if undead

                                if (damage)
                                    collidingEntity.attack(new EntityDamageByEntityEvent(this, collidingEntity, EntityDamageEvent.DamageCause.MAGIC, (float) (0.5 * (double) (6 << (effect.getAmplifier() + 1)))));
                                else
                                    collidingEntity.heal(new EntityRegainHealthEvent(collidingEntity, (float) (0.5 * (double) (4 << (effect.getAmplifier() + 1))), EntityRegainHealthEvent.CAUSE_MAGIC));

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
            setRadius(radius, false);
            kill();
        } else {
            setRadius(radius, sendRadius);
        }
        
        float height = getHeight();
        boundingBox.setBounds(x - radius, y - height, z - radius, x + radius, y + height, z + radius);
        this.setDataProperty(new FloatEntityData(DATA_BOUNDING_BOX_HEIGHT, height), false);
        this.setDataProperty(new FloatEntityData(DATA_BOUNDING_BOX_WIDTH, radius), false);
        
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
    protected float getGravity() {
        return 0;
    }
    
    @Override
    protected float getDrag() {
        return 0;
    }
    
    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }
}

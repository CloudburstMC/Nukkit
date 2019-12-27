package cn.nukkit.entity.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.InstantEffect;
import cn.nukkit.potion.Potion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EntityAreaEffectCloud extends Entity {
    public static final int NETWORK_ID = 95;
    
    protected int color;
    protected int duration;
    protected int reapplicationDelay;
    protected int waitTime;
    protected int durationOnUse;
    protected float initialRadius;
    protected float radius;
    protected float radiusChangeOnPickup;
    protected float radiusOnUse;
    protected float radiusPerTick;
    protected int displayedPotionId;
    protected int nextApply;
    public List<Effect> cloudEffects;
    private int lastAge;
    protected Particle particle;
    
    public EntityAreaEffectCloud(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
    
    @Override
    protected void initEntity() {
        super.initEntity();
        this.invulnerable = true;
        
        cloudEffects = new ArrayList<>(1);
        for (CompoundTag effectTag : namedTag.getList("mobEffects", CompoundTag.class).getAll()) {
            Effect effect = Effect.getEffect(effectTag.getByte("Id"))
                    .setAmbient(effectTag.getBoolean("Ambient"))
                    .setAmplifier(effectTag.getByte("Amplifier"))
                    .setVisible(effectTag.getBoolean("DisplayOnScreenTextureAnimation"))
                    .setDuration(effectTag.getInt("Duration"));
            cloudEffects.add(effect);
        }
        displayedPotionId = namedTag.getShort("PotionId");
        int r;
        int g;
        int b;
        
        if (namedTag.contains("ParticleColor")) {
            color = namedTag.getInt("ParticleColor");
            r = color & 0xFF0000;
            g = color & 0x00FF00;
            b = color & 0x0000FF;
        } else {
            Effect effect = Potion.getEffect(displayedPotionId, true);
    
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
    
        //particle = new InstantSpellParticle(this, r, g, b);
        particle = new GenericParticle(this, Particle.TYPE_MOB_SPELL, ((0 & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff));
        
        if (namedTag.contains("Duration")) {
            duration = namedTag.getInt("Duration");
        } else {
            duration = 600;
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
        if (namedTag.contains("Radius")) {
            radius = namedTag.getFloat("Radius");
        } else {
            radius = 0;
        }
        if (namedTag.contains("InitialRadius")) {
            initialRadius = namedTag.getFloat("InitialRadius");
        } else {
            initialRadius = 1.5F;
        }
        if (namedTag.contains("RadiusChangeOnPickup")) {
            radiusChangeOnPickup = namedTag.getFloat("RadiusChangeOnPickup");
        } else {
            radiusChangeOnPickup = -0.5F;
        }
        if (namedTag.contains("RadiusOnUse")) {
            radiusOnUse = namedTag.getFloat("RadiusOnUse");
        } else {
            radiusOnUse = -0.5F;
        }
        if (namedTag.contains("RadiusPerTick")) {
            radiusPerTick = namedTag.getFloat("RadiusPerTick");
        } else {
            radiusPerTick = -0.005F;
        }
        if (namedTag.contains("WaitTime")) {
            waitTime = namedTag.getInt("WaitTime");
        } else {
            waitTime = 10;
        }
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
        namedTag.putList(effectsTag);
        namedTag.putInt("ParticleColor", color);
        namedTag.putShort("PotionId", displayedPotionId);
        namedTag.putInt("Duration", duration);
        namedTag.putInt("DurationOnUse", durationOnUse);
        namedTag.putInt("ReapplicationDelay", reapplicationDelay);
        namedTag.putFloat("Radius", radius);
        namedTag.putFloat("RadiusChangeOnPickup", radiusChangeOnPickup);
        namedTag.putFloat("RadiusOnUse", radiusOnUse);
        namedTag.putFloat("RadiusPerTick", radiusPerTick);
        namedTag.putInt("WaitTime", waitTime);
        namedTag.putFloat("InitialRadius", initialRadius);
    }
    
    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }
    
        this.timing.startTiming();
    
        boolean hasUpdate = super.onUpdate(currentTick);
    
        int age = this.age;
        if (age < waitTime) {
            radius = ((float) age / waitTime) * initialRadius;
            hasUpdate = true;
        } else if (age > waitTime + duration) {
            kill();
            hasUpdate = true;
        } else {
            radius += radiusPerTick;
            hasUpdate = true;
            if ((nextApply -= age - lastAge) <= 0) {
                nextApply = reapplicationDelay + 5;
                Entity[] collidingEntities = level.getCollidingEntities(getBoundingBox());
                if (collidingEntities.length > 1) {
                    radius += radiusOnUse;
                    duration += durationOnUse;
                    for (Entity collidingEntity : collidingEntities) {
                        if (collidingEntity != this && collidingEntity instanceof EntityLiving) {
                            for (Effect effect : cloudEffects) {
                                if (effect instanceof InstantEffect) {
                                    switch (effect.getId()) {
                                        case Effect.HEALING:
                                            collidingEntity.heal(new EntityRegainHealthEvent(collidingEntity, (float) (0.5 * (double) (4 << (effect.getAmplifier() + 1))), EntityRegainHealthEvent.CAUSE_MAGIC));
                                            break;
                                        case Effect.HARMING:
                                            collidingEntity.attack(new EntityDamageByEntityEvent(this, collidingEntity, EntityDamageEvent.DamageCause.MAGIC, (float) (0.5 * (double) (6 << (effect.getAmplifier() + 1)))));
                                            break;
                                    }
                                } else {
                                    collidingEntity.addEffect(effect.clone());
                                }
                            }
                        }
                    }
                }
            }
        }
        
        this.lastAge = age;
        
        if (hasUpdate) {
            if (radius <= 0) {
                radius = 0.001F;
                kill();
            }
            boundingBox.setBounds(x - radius, y -getHeight(), z - radius, x + radius, y + getHeight(), z + radius);
        }
    
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (double x = -radius; x < radius; x++) {
            for (double z = -radius; z < radius; z++) {
                int count = random.nextInt(33);
                for (int i = 30; i < count; i++) {
                    Particle particle = (Particle) this.particle.clone();
                    particle.x = this.x + x + random.nextDouble();
                    particle.y = this.y + random.nextDouble();
                    particle.z = this.z + z + random.nextDouble();
                    level.addParticle(particle);
                }
            }
        }
        
        this.timing.stopTiming();
        
        return hasUpdate;
    }
    
    @Override
    public float getHeight() {
        return Math.max(1f, radius);
    }
    
    @Override
    public float getWidth() {
        return radius;
    }
    
    @Override
    public float getLength() {
        return radius;
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

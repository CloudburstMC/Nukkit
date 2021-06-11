package cn.nukkit.entity.item;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.potion.PotionCollideEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.level.particle.SpellParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;

/**
 * @author xtypr
 */
public class EntityPotion extends EntityProjectile {

    public static final int NETWORK_ID = 86;

    public static final int DATA_POTION_ID = 37;

    public int potionId;

    public EntityPotion(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public EntityPotion(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        potionId = this.namedTag.getShort("PotionId");

        this.dataProperties.putShort(DATA_POTION_AUX_VALUE, this.potionId);

        /*Effect effect = Potion.getEffect(potionId, true); TODO: potion color

        if(effect != null) {
            int count = 0;
            int[] c = effect.getColor();
            count += effect.getAmplifier() + 1;

            int r = ((c[0] * (effect.getAmplifier() + 1)) / count) & 0xff;
            int g = ((c[1] * (effect.getAmplifier() + 1)) / count) & 0xff;
            int b = ((c[2] * (effect.getAmplifier() + 1)) / count) & 0xff;

            this.setDataProperty(new IntEntityData(Entity.DATA_UNKNOWN, (r << 16) + (g << 8) + b));
        }*/
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
        return 0.05f;
    }

    @Override
    protected float getDrag() {
        return 0.01f;
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        this.splash(entity);
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    protected void splash(Entity collidedWith) {
        Potion potion = Potion.getPotion(this.potionId);
        PotionCollideEvent event = new PotionCollideEvent(potion, this);
        this.server.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        this.close();

        potion = event.getPotion();
        if (potion == null) {
            return;
        }

        potion.setSplash(true);

        Particle particle;
        int r;
        int g;
        int b;

        Effect effect = Potion.getEffect(potion.getId(), true);

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

        particle = new SpellParticle(this, r, g, b);

        this.getLevel().addParticle(particle);
        this.getLevel().addSound(this, Sound.RANDOM_GLASS);

        Entity[] entities = this.getLevel().getNearbyEntities(this.getBoundingBox().grow(4.125, 2.125, 4.125));
        for (Entity anEntity : entities) {
            double distance = anEntity.distanceSquared(this);
            if (distance < 16) {
                double d = anEntity.equals(collidedWith) ? 1 : 1 - Math.sqrt(distance) / 4;
                potion.applyPotion(anEntity, d);
            }
        }
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
        } else if (this.isCollided) {
            this.splash(null);
            hasUpdate = true;
        }

        this.timing.stopTiming();
        return hasUpdate;
    }
    
    
    @Override
    public String getName() {
        return "Potion";
    }
}

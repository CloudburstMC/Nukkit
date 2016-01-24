package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.event.potion.PotionCollideEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.InstantSpellParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.level.particle.SpellParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

/**
 * Created on 2015/12/27 by xtypr.
 * Package cn.nukkit.entity in project Nukkit .
 */
public class ThrownPotion extends Projectile {

    public static final int NETWORK_ID = 86;

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

    public ThrownPotion(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public ThrownPotion(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
        setPotionType(getPotionType()); //in order to set data property.
    }

    public int getPotionType() {
        return namedTag.getInt("Potion");
    }

    public void setPotionType(int potionType) {
        namedTag.putInt("Potion", potionType);
        setDataProperty(DATA_POTION_TYPE, new ShortEntityData(potionType));
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        //this.timings.startTiming();

        int tickDiff = currentTick - this.lastUpdate;
        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.age > 1200) {
            this.kill();
            hasUpdate = true;
        }

        if (this.isCollided) {
            this.kill();

            Potion potion = Potion.getPotion(getPotionType());

            PotionCollideEvent event = new PotionCollideEvent(potion, this);
            this.server.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return false;
            }

            potion = event.getPotion();
            if (potion == null) {
                return false;
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

            if (Potion.isInstant(potion.getId())) {
                particle = new InstantSpellParticle(this, r, g, b);
            } else {
                particle = new SpellParticle(this, r, g, b);
            }

            this.getLevel().addParticle(particle);

            hasUpdate = true;
            Entity[] entities = this.getLevel().getNearbyEntities(this.getBoundingBox().grow(8.25, 4.24, 8.25));
            for (Entity anEntity : entities) {
                potion.applyPotion(anEntity);
            }
        }

        return hasUpdate;
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.type = ThrownPotion.NETWORK_ID;
        pk.eid = this.getId();
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = (float) this.motionX;
        pk.speedY = (float) this.motionY;
        pk.speedZ = (float) this.motionZ;
        pk.metadata = this.dataProperties;
        player.dataPacket(pk);

        super.spawnTo(player);
    }
}

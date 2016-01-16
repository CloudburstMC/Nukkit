package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.potion.Potion;

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
            Potion potion = Potion.getPotion(getPotionType()).setSplashPotion();
            potion.thrownPotionCollide(this);
            hasUpdate = true;
            Entity[] entities = this.getLevel().getNearbyEntities(this.getBoundingBox().grow(8.25, 4.24, 8.25));
            for (Entity anEntity : entities) {
                potion.applyTo(anEntity);
            }
        }

        //this.timings.stopTiming();

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

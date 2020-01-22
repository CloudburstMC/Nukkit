package cn.nukkit.entity.impl.misc;

import cn.nukkit.Server;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.misc.FireworksRocket;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.Random;

import static cn.nukkit.entity.data.EntityData.*;

/**
 * @author CreeperFace
 */
public class EntityFireworksRocket extends BaseEntity implements FireworksRocket {

    private int fireworkAge;
    private int lifetime;
    private Item firework;

    public EntityFireworksRocket(EntityType<FireworksRocket> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);

        this.fireworkAge = 0;
        Random rand = new Random();
        this.lifetime = 30 + rand.nextInt(6) + rand.nextInt(7);

        this.motionX = rand.nextGaussian() * 0.001D;
        this.motionZ = rand.nextGaussian() * 0.001D;
        this.motionY = 0.05D;

        if (nbt.contains("FireworkItem")) {
            firework = NBTIO.getItemHelper(nbt.getCompound("FireworkItem"));
        } else {
            firework = Item.get(ItemIds.FIREWORKS);
        }

        this.setTagData(DISPLAY_ITEM, firework.getNamedTag());
        this.setIntData(DISPLAY_OFFSET, 1);
        this.setByteData(HAS_DISPLAY, 1);
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

        this.timing.startTiming();


        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {

            this.motionX *= 1.15D;
            this.motionZ *= 1.15D;
            this.motionY += 0.04D;
            this.move(this.motionX, this.motionY, this.motionZ);

            this.updateMovement();


            float f = (float) Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.yaw = (float) (Math.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

            this.pitch = (float) (Math.atan2(this.motionY, (double) f) * (180D / Math.PI));


            if (this.fireworkAge == 0) {
                this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_LAUNCH);
            }

            this.fireworkAge++;

            hasUpdate = true;
            if (this.fireworkAge >= this.lifetime) {
                EntityEventPacket pk = new EntityEventPacket();
                pk.data = 0;
                pk.event = EntityEventPacket.FIREWORK_EXPLOSION;
                pk.entityRuntimeId = this.getUniqueId();

                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_LARGE_BLAST, -1, getType());

                Server.broadcastPacket(getViewers(), pk);

                this.kill();
                hasUpdate = true;
            }
        }

        this.timing.stopTiming();

        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return (source.getCause() == DamageCause.VOID ||
                source.getCause() == DamageCause.FIRE_TICK ||
                source.getCause() == DamageCause.ENTITY_EXPLOSION ||
                source.getCause() == DamageCause.BLOCK_EXPLOSION)
                && super.attack(source);
    }

    public void setFirework(Item item) {
        this.firework = item;
        this.setTagData(DISPLAY_ITEM, item.getNamedTag());
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }
}

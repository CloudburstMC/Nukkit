package cn.nukkit.entity.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.NBTEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author CreeperFace
 */
public class EntityFirework extends Entity {

    public static final int NETWORK_ID = 72;

    private int lifetime;
    private Item firework;

    public EntityFirework(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void initEntity() {
        super.initEntity();

        ThreadLocalRandom rand = ThreadLocalRandom.current();

        this.motionX = rand.nextGaussian() * 0.001D;
        this.motionZ = rand.nextGaussian() * 0.001D;
        this.motionY = 0.05D;

        if (namedTag.contains("FireworkItem")) {
            this.setFirework(NBTIO.getItemHelper(namedTag.getCompound("FireworkItem")));
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (this.firework != null) {
            this.namedTag.putCompound("FireworkItem", NBTIO.putItemHelper(this.firework));
        }
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        } else if (this.age > this.lifetime) {
            this.close();
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }

        this.lastUpdate = currentTick;

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {
            this.motionX *= 1.15D;
            this.motionZ *= 1.15D;
            this.motionY += 0.04D;

            this.move(this.motionX, this.motionY, this.motionZ);

            this.updateMovement();

            float f = (float) Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.yaw = (float) (Math.atan2(this.motionX, this.motionZ) * (57.29577951308232));
            this.pitch = (float) (Math.atan2(this.motionY, f) * (57.29577951308232));

            if (this.age == 0) {
                this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_LAUNCH);
            }

            if (this.age >= this.lifetime) {
                EntityEventPacket pk = new EntityEventPacket();
                pk.event = EntityEventPacket.FIREWORK_EXPLOSION;
                pk.eid = this.getId();
                this.level.addChunkPacket(this.getChunkX(), this.getChunkZ(), pk);

                this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_LARGE_BLAST, -1, NETWORK_ID);

                if (this.firework != null) {
                    Tag nbt = this.firework.getNamedTag();
                    if (nbt != null) {
                        nbt = ((CompoundTag) nbt).get("Fireworks");
                        if (nbt instanceof CompoundTag) {
                            nbt = ((CompoundTag) nbt).get("Explosions");
                            if (nbt instanceof ListTag) {
                                if (((ListTag) nbt).size() != 0) {
                                    EntityExplosionPrimeEvent ev = new EntityExplosionPrimeEvent(this, 2.5);
                                    ev.setBlockBreaking(false);
                                    server.getPluginManager().callEvent(ev);
                                    if (!ev.isCancelled()) {
                                        Explosion explosion = new Explosion(this, ev.getForce(), this);
                                        explosion.explodeEntity();
                                    }
                                }
                            }
                        }
                    }
                }

                this.kill(); // Using close() here would remove the firework before the explosion is displayed

                hasUpdate = true;
            }
        }

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
        this.setDataProperty(new NBTEntityData(Entity.DATA_DISPLAY_ITEM, this.firework));

        int level = Math.max(1, this.firework instanceof ItemFirework ? ((ItemFirework) this.firework).getFlight() : 1); // Level 1 minimum
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        this.lifetime = 10 * (level + 1) + rand.nextInt(5) + rand.nextInt(6); // Wiki
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
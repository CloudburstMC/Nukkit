package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.entity.data.SlotEntityData;
import cn.nukkit.entity.data.Vector3fEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFireworks;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.PlaySoundPacket;

/**
 * @author CreeperFace
 */
public class EntityFirework extends Entity {

    public static final int NETWORK_ID = 72;

    private int fireworkAge;
    private int lifetime;
    private Item firework;

    public EntityFirework(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.fireworkAge = 0;
        this.lifetime = 30 + this.level.rand.nextInt(6) + this.level.rand.nextInt(7);

        this.motionX = this.level.rand.nextGaussian() * 0.001D;
        this.motionZ = this.level.rand.nextGaussian() * 0.001D;
        this.motionY = 0.05D;

        if (nbt.contains("FireworkItem")) {
            firework = NBTIO.getItemHelper(nbt.getCompound("FireworkItem"));
        } else {
            firework = new ItemFireworks();
        }

        this.setDataProperty(new SlotEntityData(16, firework));
        this.setDataProperty(new Vector3fEntityData(17, new Vector3f(0, 1, 0)));
        this.setDataProperty(new LongEntityData(18, -1));
        this.setDataProperty(new ByteEntityData(22, 0));
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
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
            if (this.checkObstruction(this.x, this.y, this.z)) {
                hasUpdate = true;
            }

            this.motionX *= 1.15D;
            this.motionZ *= 1.15D;
            this.motionY += 0.04D;
            this.move(this.motionX, this.motionY, this.motionZ);

            this.updateMovement();

            float f = (float) Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.yaw = (float) (Math.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

            for (this.pitch = (float) (Math.atan2(this.motionY, (double) f) * (180D / Math.PI)); this.pitch - this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
                ;
            }

            if (this.fireworkAge == 0) {
                PlaySoundPacket pk = new PlaySoundPacket();
                pk.name = "firework.launch";
                pk.volume = 1;
                pk.pitch = 1;
                pk.x = getFloorX();
                pk.y = getFloorY();
                pk.z = getFloorZ();

                this.level.addChunkPacket(this.getFloorX() >> 4, this.getFloorZ() >> 4, pk);
            }

            this.fireworkAge++;

            hasUpdate = true;
            if (this.fireworkAge >= this.lifetime) {
                EntityEventPacket pk = new EntityEventPacket();
                pk.data = 0;
                pk.event = EntityEventPacket.FIREWORK_EXPLOSION;
                pk.eid = this.getId();

                LevelSoundEventPacket pk2 = new LevelSoundEventPacket();
                pk2.sound = LevelSoundEventPacket.SOUND_LARGE_BLAST;
                pk2.extraData = -1;
                pk2.pitch = -1;
                pk2.x = (float) getX();
                pk2.y = (float) getY();
                pk2.z = (float) getZ();

                Server.broadcastPacket(getViewers().values(), pk);
                this.level.addChunkPacket(this.getFloorX() >> 4, this.getFloorZ() >> 4, pk2);

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
        this.setDataProperty(new SlotEntityData(16, item));
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);

        AddEntityPacket pk = new AddEntityPacket();
        pk.type = NETWORK_ID;
        pk.entityUniqueId = this.getId();
        pk.entityRuntimeId = this.getId();
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = (float) this.motionX;
        pk.speedY = (float) this.motionY;
        pk.speedZ = (float) this.motionZ;
        pk.metadata = this.dataProperties;
        player.dataPacket(pk);
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

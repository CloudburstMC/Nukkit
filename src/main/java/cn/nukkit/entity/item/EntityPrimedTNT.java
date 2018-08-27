package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

/**
 * @author MagicDroidX
 */
public class EntityPrimedTNT extends Entity implements EntityExplosive {

    public static final int NETWORK_ID = 65;

    @Override
    public float getWidth() {
        return 0.98f;
    }

    @Override
    public float getLength() {
        return 0.98f;
    }

    @Override
    public float getHeight() {
        return 0.98f;
    }

    @Override
    protected float getGravity() {
        return 0.04f;
    }

    @Override
    protected float getDrag() {
        return 0.02f;
    }

    @Override
    protected float getBaseOffset() {
        return 0.49f;
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    protected int fuse;

    protected Entity source;

    public EntityPrimedTNT(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityPrimedTNT(FullChunk chunk, CompoundTag nbt, Entity source) {
        super(chunk, nbt);
        this.source = source;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    protected void initEntity() {
        super.initEntity();

        if (namedTag.contains("Fuse")) {
            fuse = namedTag.getByte("Fuse");
        } else {
            fuse = 80;
        }

        this.setDataFlag(DATA_FLAGS, DATA_FLAG_IGNITED, true);
        this.setDataProperty(new IntEntityData(DATA_FUSE_LENGTH, fuse));

        this.level.addSound(this, Sound.RANDOM_FIZZ);
    }


    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public void saveNBT() {
        super.saveNBT();
        namedTag.putByte("Fuse", fuse);
    }

    public boolean onUpdate(int currentTick) {

        if (closed) {
            return false;
        }

        this.timing.startTiming();

        int tickDiff = currentTick - lastUpdate;

        if (tickDiff <= 0 && !justCreated) {
            return true;
        }

        if (fuse % 5 == 0) {
            this.setDataProperty(new IntEntityData(DATA_FUSE_LENGTH, fuse));
        }

        this.lastUpdate = currentTick;

        boolean hasUpdate = entityBaseTick(tickDiff);

        if (this.isAlive()) {

            this.motionY -= getGravity();

            this.move(this.motionX, this.motionY, this.motionZ);

            float friction = 1 - getDrag();

            this.motionX *= friction;
            this.motionY *= friction;
            this.motionZ *= friction;

            this.updateMovement();

            if (this.onGround) {
                this.motionY *= -0.5;
                this.motionX *= 0.7;
                this.motionZ *= 0.7;
            }

            fuse -= tickDiff;

            if (fuse <= 0) {
                if (this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES))
                    explode();
                this.kill();
            }

        }

        this.timing.stopTiming();

        return hasUpdate || fuse >= 0 || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    public void explode() {
        EntityExplosionPrimeEvent event = new EntityExplosionPrimeEvent(this, 4);
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        Explosion explosion = new Explosion(this, event.getForce(), this);
        if (event.isBlockBreaking()) {
            explosion.explodeA();
        }
        explosion.explodeB();
    }

    public void spawnTo(Player player) {
        AddEntityPacket packet = new AddEntityPacket();
        packet.type = this.getNetworkId();
        packet.entityUniqueId = this.getId();
        packet.entityRuntimeId = this.getId();
        packet.x = (float) this.x;
        packet.y = (float) this.y;
        packet.z = (float) this.z;
        packet.speedX = (float) this.motionX;
        packet.speedY = (float) this.motionY;
        packet.speedZ = (float) this.motionZ;
        packet.metadata = this.dataProperties;
        player.dataPacket(packet);
        super.spawnTo(player);
    }

    public Entity getSource() {
        return source;
    }
}
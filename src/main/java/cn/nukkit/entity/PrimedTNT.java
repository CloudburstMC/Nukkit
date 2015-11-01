package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

/**
 * Created on 15-10-27.
 */
public class PrimedTNT extends Entity implements Explosive {

    public static final int NETWORK_ID = 65;

    private float width = 0.98F;
    private float length = 0.98F;
    private float height = 0.98F;

    protected float gravity = 0.04F;
    protected float drag = 0.02F;

    protected int fuse;

    private boolean canCollide;

    public PrimedTNT(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public void attack(float damage, EntityDamageEvent source) {
        if (source.getCause() == EntityDamageEvent.CAUSE_VOID) {
            super.attack(damage, source);
        }
    }

    protected void initEntity() {
        super.initEntity();

        if (namedTag.contains("Fuse")) {
            fuse = namedTag.getByte("Fuse") & 0xff;
        } else {
            fuse = 80;
        }
    }


    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public void saveNBT() {
        super.saveNBT();
        namedTag.putByte("Fuse", (byte) fuse);
    }

    public boolean onUpdate(int currentTick) {

        if (closed) {
            return false;
        }

        // TODO timings.startTiming();

        int tickDiff = currentTick - lastUpdate;

        if (tickDiff <= 0 && !justCreated) {
            return true;
        }
        lastUpdate = currentTick;

        boolean hasUpdate = entityBaseTick(tickDiff);

        if (isAlive()) {

            motionY -= gravity;

            move(motionX, motionY, motionZ);

            float friction = 1 - drag;

            motionX *= friction;
            motionY *= friction;
            motionZ *= friction;

            updateMovement();

            if (onGround) {
                motionY *= -0.5;
                motionX *= 0.7;
                motionZ *= 0.7;
            }

            fuse -= tickDiff;

            if (fuse <= 0) {
                kill();
                explode();
            }

        }
        return hasUpdate || fuse >= 0 || Math.abs(motionX) > 0.00001 || Math.abs(motionY) > 0.00001 || Math.abs(motionZ) > 0.00001;
    }

    public void explode() {
        EntityExplosionPrimeEvent event = new EntityExplosionPrimeEvent(this, 4);
        server.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            Explosion explosion = new Explosion(this, event.getForce(), this);
            if (event.isBlockBreaking()) {
                explosion.explodeA();
            }
            explosion.explodeB();
        }
    }

    public void spawnTo(Player player) {
        AddEntityPacket packet = new AddEntityPacket();
        packet.type = PrimedTNT.NETWORK_ID;
        packet.eid = getId();
        packet.x = (float) x;
        packet.y = (float) y;
        packet.z = (float) z;
        packet.speedX = (float) motionX;
        packet.speedY = (float) motionY;
        packet.speedZ = (float) motionZ;
        packet.metadata = dataProperties;
        player.dataPacket(packet);
        super.spawnTo(player);
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isCanCollide() {
        return canCollide;
    }

    public void setCanCollide(boolean canCollide) {
        this.canCollide = canCollide;
    }

}
package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.ItemDespawnEvent;
import cn.nukkit.event.entity.ItemSpawnEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddItemEntityPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DroppedItem extends Entity {
    public static final int NETWORK_ID = 64;

    public DroppedItem(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    protected String owner = null;
    protected String thrower = null;

    protected Item item;

    protected int pickupDelay = 0;

    public float width = 0.25f;
    public float length = 0.25f;
    public float height = 0.25f;
    protected float gravity = 0.04f;
    protected float drag = 0.02f;

    public boolean canCollide = false;

    @Override
    protected void initEntity() {
        super.initEntity();

        this.setMaxHealth(5);
        this.setHealth(this.namedTag.getShort("Health"));

        if (this.namedTag.contains("Age")) {
            this.age = this.namedTag.getShort("Age");
        }

        if (this.namedTag.contains("PickupDelay")) {
            this.pickupDelay = this.namedTag.getShort("PickupDelay");
        }

        if (this.namedTag.contains("Owner")) {
            this.owner = this.namedTag.getString("Owner");
        }

        if (this.namedTag.contains("Thrower")) {
            this.thrower = this.namedTag.getString("Thrower");
        }

        if (!this.namedTag.contains("Item")) {
            this.close();
            return;
        }

        this.item = NBTIO.getItemHelper(this.namedTag.getCompound("Item"));

        this.server.getPluginManager().callEvent(new ItemSpawnEvent(this));
    }

    @Override
    public void attack(EntityDamageEvent source) {
        if (source.getCause() == EntityDamageEvent.CAUSE_VOID ||
                source.getCause() == EntityDamageEvent.CAUSE_FIRE_TICK ||
                source.getCause() == EntityDamageEvent.CAUSE_ENTITY_EXPLOSION ||
                source.getCause() == EntityDamageEvent.CAUSE_BLOCK_EXPLOSION
                ) {
            super.attack(source);
        }
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

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {

            if (this.pickupDelay > 0 && this.pickupDelay < 32767) {
                this.pickupDelay -= tickDiff;
                if (this.pickupDelay < 0) {
                    this.pickupDelay = 0;
                }
            }

            this.motionY -= this.gravity;

            if (this.checkObstruction(this.x, this.y, this.z)) {
                hasUpdate = true;
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            double friction = 1 - this.drag;

            if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
                friction *= this.getLevel().getBlock(this.temporalVector.setComponents((int) Math.floor(this.x), (int) Math.floor(this.y - 1), (int) Math.floor(this.z) - 1)).getFrictionFactor();
            }

            this.motionX *= friction;
            this.motionY *= 1 - this.drag;
            this.motionZ *= friction;

            if (this.onGround) {
                this.motionY *= -0.5;
            }

            this.updateMovement();

            if (this.age > 6000) {
                ItemDespawnEvent ev = new ItemDespawnEvent(this);
                this.server.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    this.age = 0;
                } else {
                    this.kill();
                    hasUpdate = true;
                }
            }
        }

        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putCompound("Item", new CompoundTag()
                .putShort("id", this.item.getId())
                .putShort("Damage", this.item.getDamage())
                .putByte("Count", (byte) this.item.getCount())
        );

        this.namedTag.putShort("Health", this.getHealth());
        this.namedTag.putShort("Age", this.age);
        this.namedTag.putShort("PickupDelay", this.pickupDelay);
        if (this.owner != null) {
            this.namedTag.putString("Owner", this.owner);
        }

        if (this.thrower != null) {
            this.namedTag.putString("Thrower", this.thrower);
        }
    }

    public Item getItem() {
        return item;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public int getPickupDelay() {
        return pickupDelay;
    }

    public void setPickupDelay(int pickupDelay) {
        this.pickupDelay = pickupDelay;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getThrower() {
        return thrower;
    }

    public void setThrower(String thrower) {
        this.thrower = thrower;
    }

    @Override
    public void spawnTo(Player player) {
        AddItemEntityPacket pk = new AddItemEntityPacket();
        pk.eid = this.getId();
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = (float) this.motionX;
        pk.speedY = (float) this.motionY;
        pk.speedZ = (float) this.motionZ;
        pk.item = this.getItem();
        player.dataPacket(pk);

        this.sendData(player);

        super.spawnTo(player);
    }
}

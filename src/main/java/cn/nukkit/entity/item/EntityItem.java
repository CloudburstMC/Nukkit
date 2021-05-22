package cn.nukkit.entity.item;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.ItemDespawnEvent;
import cn.nukkit.event.entity.ItemSpawnEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddItemEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.EntityEventPacket;

/**
 * @author MagicDroidX
 */
public class EntityItem extends Entity {
    public static final int NETWORK_ID = 64;

    public static final int DATA_SOURCE_ID = 17;

    public EntityItem(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    protected String owner;
    protected String thrower;

    protected Item item;

    protected int pickupDelay;

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
    public float getGravity() {
        return 0.04f;
    }

    @Override
    public float getDrag() {
        return 0.02f;
    }

    @Override
    protected float getBaseOffset() {
        return 0.125f;
    }

    @Override
    public boolean canCollide() {
        return false;
    }

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
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_GRAVITY, true);

        if (this.item.isLavaResistant()) {
            this.fireProof = true; // Netherite items are fireproof
        }

        this.server.getPluginManager().callEvent(new ItemSpawnEvent(this));
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Netherite stuff is immune to fire and lava damage")
    @Override
    public boolean attack(EntityDamageEvent source) {
        if (item != null && item.isLavaResistant() && (
                source.getCause() == DamageCause.LAVA ||
                        source.getCause() == DamageCause.FIRE ||
                        source.getCause() == DamageCause.FIRE_TICK)) {
            return false;
        }

        return (source.getCause() == DamageCause.VOID ||
                source.getCause() == DamageCause.CONTACT ||
                source.getCause() == DamageCause.FIRE_TICK ||
                (source.getCause() == DamageCause.ENTITY_EXPLOSION ||
                source.getCause() == DamageCause.BLOCK_EXPLOSION) &&
                !this.isInsideOfWater() && (this.item == null ||
                this.item.getId() != Item.NETHER_STAR)) && super.attack(source);
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

        if (this.age % 60 == 0 && this.onGround && this.getItem() != null && this.isAlive()) {
            if (this.getItem().getCount() < this.getItem().getMaxStackSize()) {
                for (Entity entity : this.getLevel().getNearbyEntities(getBoundingBox().grow(1, 1, 1), this, false)) {
                    if (entity instanceof EntityItem) {
                        if (!entity.isAlive()) {
                            continue;
                        }
                        Item closeItem = ((EntityItem) entity).getItem();
                        if (!closeItem.equals(getItem(), true, true)) {
                            continue;
                        }
                        if (!entity.isOnGround()) {
                            continue;
                        }
                        int newAmount = this.getItem().getCount() + closeItem.getCount();
                        if (newAmount > this.getItem().getMaxStackSize()) {
                            continue;
                        }
                        entity.close();
                        this.getItem().setCount(newAmount);
                        EntityEventPacket packet = new EntityEventPacket();
                        packet.eid = getId();
                        packet.data = newAmount;
                        packet.event = EntityEventPacket.MERGE_ITEMS;
                        Server.broadcastPacket(this.getViewers().values(), packet);
                    }
                }
            }
        }

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        boolean lavaResistant = fireProof || item != null && item.isLavaResistant();

        if (!lavaResistant && (isInsideOfFire() || isInsideOfLava())) {
            this.kill();
        }

        if (this.isAlive()) {
            if (this.pickupDelay > 0 && this.pickupDelay < 32767) {
                this.pickupDelay -= tickDiff;
                if (this.pickupDelay < 0) {
                    this.pickupDelay = 0;
                }
            }/* else { // Done in Player#checkNearEntities
                for (Entity entity : this.level.getNearbyEntities(this.boundingBox.grow(1, 0.5, 1), this)) {
                    if (entity instanceof Player) {
                        if (((Player) entity).pickupEntity(this, true)) {
                            return true;
                        }
                    }
                }
            }*/

            int bid = this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 0);
            if (bid == BlockID.WATER || bid == BlockID.STILL_WATER
                || (bid = this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 1)) == BlockID.WATER
                || bid == BlockID.STILL_WATER
            ) {
                //item is fully in water or in still water
                this.motionY -= this.getGravity() * -0.015;
            } else if (lavaResistant && (
                    this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 0) == BlockID.LAVA
                            || this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 0) == BlockID.STILL_LAVA
                            || this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 1) == BlockID.LAVA
                            || this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 1) == BlockID.STILL_LAVA
            )) {
                //item is fully in lava or in still lava
                this.motionY -= this.getGravity() * -0.015;
            } else if (this.isInsideOfWater() || lavaResistant && this.isInsideOfLava()) {
                this.motionY = this.getGravity() - 0.06; //item is going up in water, don't let it go back down too fast
            } else {
                this.motionY -= this.getGravity(); //item is not in water
            }

            if (this.checkObstruction(this.x, this.y, this.z)) {
                hasUpdate = true;
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            double friction = 1 - this.getDrag();

            if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
                friction *= this.getLevel().getBlock(this.temporalVector.setComponents((int) Math.floor(this.x), (int) Math.floor(this.y - 1), (int) Math.floor(this.z) - 1)).getFrictionFactor();
            }

            this.motionX *= friction;
            this.motionY *= 1 - this.getDrag();
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

        this.timing.stopTiming();

        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    @Override
    public void setOnFire(int seconds) {
        if (item != null && item.isLavaResistant()) {
            return;
        }
        super.setOnFire(seconds);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        if (this.item != null) { // Yes, a item can be null... I don't know what causes this, but it can happen.
            this.namedTag.putCompound("Item", NBTIO.putItemHelper(this.item, -1));
            this.namedTag.putShort("Health", (int) this.getHealth());
            this.namedTag.putShort("Age", this.age);
            this.namedTag.putShort("PickupDelay", this.pickupDelay);
            if (this.owner != null) {
                this.namedTag.putString("Owner", this.owner);
            }

            if (this.thrower != null) {
                this.namedTag.putString("Thrower", this.thrower);
            }
        }
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : (this.item.hasCustomName() ? this.item.getCustomName() : this.item.getName());
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
    public DataPacket createAddEntityPacket() {
        AddItemEntityPacket addEntity = new AddItemEntityPacket();
        addEntity.entityUniqueId = this.getId();
        addEntity.entityRuntimeId = this.getId();
        addEntity.x = (float) this.x;
        addEntity.y = (float) this.y;
        addEntity.z = (float) this.z;
        addEntity.speedX = (float) this.motionX;
        addEntity.speedY = (float) this.motionY;
        addEntity.speedZ = (float) this.motionZ;
        addEntity.metadata = this.dataProperties;
        addEntity.item = this.getItem();
        return addEntity;
    }

    @Override
    public boolean doesTriggerPressurePlate() {
        return true;
    }
}

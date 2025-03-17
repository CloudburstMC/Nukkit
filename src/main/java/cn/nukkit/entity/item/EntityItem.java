package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.ItemDespawnEvent;
import cn.nukkit.event.entity.ItemSpawnEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.AddItemEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.EntityEventPacket;

/**
 * @author MagicDroidX
 */
public class EntityItem extends Entity {

    public static final int NETWORK_ID = 64;
    protected String owner;
    protected String thrower;
    protected Item item;
    protected int pickupDelay;
    protected boolean floatsInLava;
    public Player droppedBy;

    private boolean deadOnceAndForAll;

    public EntityItem(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.updateMode = 5;
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
        this.setMaxHealth(5);

        super.initEntity();

        if (namedTag.contains("Health")) {
            this.setHealth(namedTag.getShort("Health"));
        } else {
            this.setHealth(5);
        }

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

        try {
            this.item = NBTIO.getItemHelper(this.namedTag.getCompound("Item"));
        } catch (Exception ex) {
            server.getLogger().error("Item couldn't be loaded", ex);
            this.close();
            return;
        }

        int id = this.item.getId();
        if (id >= Item.NETHERITE_INGOT && id <= Item.NETHERITE_SCRAP) {
            this.fireProof = true; // Netherite items are fireproof
            this.floatsInLava = true;
        }

        this.server.getPluginManager().callEvent(new ItemSpawnEvent(this));
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        DamageCause cause = source.getCause();
        if ((cause == DamageCause.VOID || cause == DamageCause.CONTACT || cause == DamageCause.FIRE_TICK
                || (cause == DamageCause.ENTITY_EXPLOSION || cause == DamageCause.BLOCK_EXPLOSION) && !this.isInsideOfWater()
                && (this.item == null || this.item.getId() != Item.NETHER_STAR)) && super.attack(source)) {
            if (this.item == null || this.isAlive() || this.deadOnceAndForAll) {
                return true;
            }
            this.deadOnceAndForAll = true;
            int id = this.item.getId();
            if (id != Item.SHULKER_BOX && id != Item.UNDYED_SHULKER_BOX) {
                return true;
            }
            CompoundTag nbt = this.item.getNamedTag();
            if (nbt == null) {
                return true;
            }
            ListTag<CompoundTag> items = nbt.getList("Items", CompoundTag.class);
            for (int i = 0; i < items.size(); i++) {
                CompoundTag itemTag = items.get(i);
                Item item = NBTIO.getItemHelper(itemTag);
                if (item.isNull()) {
                    continue;
                }
                this.level.dropItem(this, item);
            }
            return true;
        }
        return false;
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

        this.minimalEntityTick(currentTick, tickDiff);

        if (this.age > 6000) {
            ItemDespawnEvent ev = new ItemDespawnEvent(this);
            this.server.getPluginManager().callEvent(ev);

            if (ev.isCancelled()) {
                this.age = 0;
            } else {
                this.close();
                return false;
            }
        }

        if (this.isAlive()) {
            if (this.pickupDelay > 0 && this.pickupDelay < 32767) {
                this.pickupDelay -= tickDiff;
                if (this.pickupDelay < 0) {
                    this.pickupDelay = 0;
                }
            }

            // updateMode % 3 or age % 20 basically means on every reduced update, don't use updateMode % 2 because that would include every update with default updateMode
            if (this.onGround && this.item != null && (this.updateMode % 3 == 1 || this.age % 20 == 0)) {
                if (this.item.getCount() < this.item.getMaxStackSize()) {
                    Entity[] e = this.getLevel().getNearbyEntities(getBoundingBox().grow(1, 1, 1), this);

                    for (Entity entity : e) {
                        if (entity instanceof EntityItem) {
                            if (entity.closed || !entity.isAlive() || !entity.isOnGround()) {
                                continue;
                            }
                            Item closeItem = ((EntityItem) entity).getItem();
                            if (!closeItem.equals(item, true, true)) {
                                continue;
                            }
                            int newAmount = this.item.getCount() + closeItem.getCount();
                            if (newAmount > this.item.getMaxStackSize()) {
                                continue;
                            }
                            closeItem.setCount(0);
                            entity.close();
                            this.item.setCount(newAmount);
                            EntityEventPacket packet = new EntityEventPacket();
                            packet.eid = getId();
                            packet.data = newAmount;
                            packet.event = EntityEventPacket.MERGE_ITEMS;
                            Server.broadcastPacket(this.getViewers().values(), packet);
                        }
                        if (this.item.getCount() >= this.item.getMaxStackSize()) {
                            break;
                        }
                    }
                }
            }

            int blockId;
            if (this.isOnGround()) {
                this.motionY = 0;
            } else if (Block.isWater((blockId = level.getBlockIdAt(this.chunk, this.getFloorX(), NukkitMath.floorDouble(this.y + 0.53), this.getFloorZ()))) ||
                    (this.floatsInLava && (blockId == Block.LAVA || blockId == Block.STILL_LAVA))) {
                this.motionY = this.getGravity() / 2;
            } else {
                this.motionY -= this.getGravity();
            }

            this.checkBlockCollision();

            this.checkObstruction(this.x, this.y, this.z);

            // Check nearby blocks changed
            if (this.updateMode % 3 == 1) {
                this.updateMode = 5;

                // Force check for nearby blocks changed even if not moving
                if (this.motionY == 0) {
                    this.motionY = 0.00001;
                }
            }

            // Flowing water
            if (this.move(this.motionX, this.motionY, this.motionZ) && !(this.motionX == 0 && this.motionZ == 0)) {
                this.collisionBlocks = null;
            }

            if (this.y < (this.getLevel().getMinBlockY() - 16)) {
                this.attack(new EntityDamageEvent(this, DamageCause.VOID, 10));
            }

            if (this.fireTicks > 0) {
                if (this.fireProof) {
                    this.fireTicks -= tickDiff << 2;
                    if (this.fireTicks < 0) {
                        this.fireTicks = 0;
                    }
                } else {
                    if (((this.fireTicks % 20) == 0 || tickDiff > 20)) {
                        this.attack(new EntityDamageEvent(this, DamageCause.FIRE_TICK, 1));
                    }
                    this.fireTicks -= tickDiff;
                }
                if (this.fireTicks <= 0) {
                    this.extinguish();
                } else if (!this.fireProof) {
                    this.setDataFlag(DATA_FLAGS, DATA_FLAG_ONFIRE, true);
                }
            }

            double friction = 1 - this.getDrag();
            if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
                friction *= this.getLevel().getBlock(this.chunk, getFloorX(), getFloorY() - 1, getFloorZ(), false).getFrictionFactor();
            }

            this.motionX *= friction;
            this.motionY *= 1 - this.getDrag();
            this.motionZ *= friction;

            if (this.onGround) {
                this.motionY = 0;
            }

            this.updateMovement();
        }

        if (!this.isAlive()) {
            this.close();
            return false;
        }

        return !(this.motionX == 0 && this.motionY == 0 && this.motionZ == 0);
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
        addEntity.y = (float) this.y + this.getBaseOffset();
        addEntity.z = (float) this.z;
        addEntity.speedX = (float) this.motionX;
        addEntity.speedY = (float) this.motionY;
        addEntity.speedZ = (float) this.motionZ;
        addEntity.metadata = this.dataProperties.clone();
        addEntity.item = this.item;
        return addEntity;
    }
}

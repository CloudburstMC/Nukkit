package cn.nukkit.entity.impl.misc;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.misc.DroppedItem;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.ItemDespawnEvent;
import cn.nukkit.event.entity.ItemSpawnEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.item.ItemUtils;
import cn.nukkit.level.Location;
import cn.nukkit.player.Player;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.EntityEventType;
import com.nukkitx.protocol.bedrock.packet.AddItemEntityPacket;
import com.nukkitx.protocol.bedrock.packet.EntityEventPacket;

import javax.annotation.Nonnull;

import static cn.nukkit.block.BlockIds.FLOWING_WATER;
import static cn.nukkit.block.BlockIds.WATER;
import static com.nukkitx.network.util.Preconditions.checkArgument;
import static com.nukkitx.network.util.Preconditions.checkNotNull;
import static com.nukkitx.protocol.bedrock.data.EntityData.OWNER_EID;

/**
 * @author MagicDroidX
 */
public class EntityDroppedItem extends BaseEntity implements DroppedItem {

    protected Item item;
    protected int pickupDelay;

    public EntityDroppedItem(EntityType<DroppedItem> type, Location location) {
        super(type, location);
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
        super.initEntity();

        this.setMaxHealth(5);

        this.server.getPluginManager().callEvent(new ItemSpawnEvent(this));
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForShort("Health", this::setHealth);
        tag.listenForShort("PickupDelay", this::setPickupDelay);
        tag.listenForShort("Age", v -> this.age = v);
        tag.listenForLong("OwnerID", v -> this.data.setLong(OWNER_EID, v));
        tag.listenForCompound("Item", itemTag -> this.item = ItemUtils.deserializeItem(itemTag));
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.shortTag("Health", (short) this.getHealth());
        tag.shortTag("PickupDelay", (short) this.pickupDelay);
        tag.shortTag("Age", (short) this.age);
        tag.longTag("OwnerID", this.data.getLong(OWNER_EID));
        tag.tag(ItemUtils.serializeItem(this.item).toBuilder().build("Item"));
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return (source.getCause() == DamageCause.VOID ||
                source.getCause() == DamageCause.CONTACT ||
                source.getCause() == DamageCause.FIRE_TICK ||
                (source.getCause() == DamageCause.ENTITY_EXPLOSION ||
                        source.getCause() == DamageCause.BLOCK_EXPLOSION) &&
                        !this.isInsideOfWater() && (this.item == null ||
                        this.item.getId() != ItemIds.NETHER_STAR)) && super.attack(source);
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
                    if (entity instanceof EntityDroppedItem) {
                        if (!entity.isAlive()) {
                            continue;
                        }
                        Item closeItem = ((EntityDroppedItem) entity).getItem();
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
                        packet.setRuntimeEntityId(this.getRuntimeId());
                        packet.setType(EntityEventType.MERGE_ITEMS);
                        packet.setData(newAmount);
                        Server.broadcastPacket(this.getLevel().getPlayers().values(), packet);
                    }
                }
            }
        }

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (isInsideOfFire()) {
            this.kill();
        }

        if (this.isAlive()) {
            if (this.pickupDelay > 0 && this.pickupDelay < 32767) {
                this.pickupDelay -= tickDiff;
                if (this.pickupDelay < 0) {
                    this.pickupDelay = 0;
                }
            } else {
                for (Entity entity : this.level.getNearbyEntities(this.boundingBox.grow(1, 0.5f, 1), this)) {
                    if (entity instanceof Player) {
                        if (((Player) entity).pickupEntity(this, true)) {
                            return true;
                        }
                    }
                }
            }

            Vector3f pos = this.getPosition();

            if (this.level.getBlockId(pos.getFloorX(), (int) this.boundingBox.getMaxY(), pos.getFloorZ()) == FLOWING_WATER ||
                    this.level.getBlockId(pos.getFloorX(), (int) this.boundingBox.getMaxY(), pos.getFloorZ()) == WATER) { //item is fully in water or in still water
                this.motion = this.motion.sub(0, this.getGravity() * -0.015, 0);
            } else if (this.isInsideOfWater()) {
                this.motion = Vector3f.from(this.motion.getX(), this.getGravity() - 0.06, this.motion.getZ()); //item is going up in water, don't let it go back down too fast
            } else {
                this.motion = this.motion.sub(0, this.getGravity(), 0); //item is not in water
            }

            if (this.checkObstruction(pos)) {
                hasUpdate = true;
            }

            this.move(this.motion);

            double friction = 1 - this.getDrag();

            if (this.onGround && (Math.abs(this.motion.getX()) > 0.00001 || Math.abs(this.motion.getZ()) > 0.00001)) {
                friction *= this.getLevel().getBlock(pos.add(0, -1, -1)).getFrictionFactor();
            }

            this.motion = this.motion.mul(friction, 1 - this.getDrag(), friction);

            if (this.onGround) {
                this.motion = this.motion.mul(1, -0.5, 1);
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

        return hasUpdate || !this.onGround || this.motion.length() > 0.00001;
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.getCustomName() : (this.item.hasCustomName() ? this.item.getCustomName() : this.item.getName());
    }

    public Item getItem() {
        return item;
    }

    @Override
    public void setItem(@Nonnull Item item) {
        checkNotNull(item, "item");
        checkArgument(this.item == null, "Item has already been set");
        this.item = item;
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

    @Override
    public BedrockPacket createAddEntityPacket() {
        AddItemEntityPacket addEntity = new AddItemEntityPacket();
        addEntity.setUniqueEntityId(this.getUniqueId());
        addEntity.setRuntimeEntityId(this.getRuntimeId());
        addEntity.setPosition(this.getPosition());
        addEntity.setMotion(this.getMotion());
        this.data.putAllIn(addEntity.getMetadata());
        addEntity.setItemInHand(this.getItem().toNetwork());
        return addEntity;
    }

    @Override
    public boolean canTriggerPressurePlate() {
        return true;
    }
}

package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.EntityArmorInventory;
import cn.nukkit.inventory.EntityEquipmentInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.SetEntityDataPacket;
import cn.nukkit.potion.Effect;

import javax.annotation.Nonnull;
import java.util.Collection;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class EntityArmorStand extends Entity implements InventoryHolder, EntityInteractable {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final int NETWORK_ID = 61;

    private static final String TAG_MAINHAND = "Mainhand";
    private static final String TAG_POSE_INDEX = "PoseIndex";
    private static final String TAG_OFFHAND = "Offhand";
    private static final String TAG_ARMOR = "Armor";

    private EntityEquipmentInventory equipmentInventory;
    private EntityArmorInventory armorInventory;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public EntityArmorStand(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        setMaxHealth(6);
        setHealth(6);

        if (nbt.contains(TAG_POSE_INDEX)) {
            this.setPose(nbt.getInt(TAG_POSE_INDEX));
        }
    }

    private static int getArmorSlot(ItemArmor armorItem) {
        if (armorItem.isHelmet()) {
            return EntityArmorInventory.SLOT_HEAD;
        } else if (armorItem.isChestplate()) {
            return EntityArmorInventory.SLOT_CHEST;
        } else if (armorItem.isLeggings()) {
            return EntityArmorInventory.SLOT_LEGS;
        } else {
            return EntityArmorInventory.SLOT_FEET;
        }
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getHeight() {
        return 1.975f;
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    protected float getGravity() {
        return 0.04f;
    }

    @Override
    protected void initEntity() {

        this.setHealth(6);
        this.setMaxHealth(6);
        this.setImmobile(true);

        super.initEntity();

        this.equipmentInventory = new EntityEquipmentInventory(this);
        this.armorInventory = new EntityArmorInventory(this);

        if (this.namedTag.contains(TAG_MAINHAND)) {
            this.equipmentInventory.setItemInHand(NBTIO.getItemHelper(this.namedTag.getCompound(TAG_MAINHAND)), true);
        }

        if (this.namedTag.contains(TAG_OFFHAND)) {
            this.equipmentInventory.setItemInOffhand(NBTIO.getItemHelper(this.namedTag.getCompound(TAG_OFFHAND)), true);
        }

        if (this.namedTag.contains(TAG_ARMOR)) {
            ListTag<CompoundTag> armorList = this.namedTag.getList(TAG_ARMOR, CompoundTag.class);
            for (CompoundTag armorTag : armorList.getAll()) {
                this.armorInventory.setItem(armorTag.getByte("Slot"), NBTIO.getItemHelper(armorTag));
            }
        }

        if (this.namedTag.contains(TAG_POSE_INDEX)) {
            this.setPose(this.namedTag.getInt(TAG_POSE_INDEX));
        }
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (player.isSpectator() || !isValid()) {
            return false;
        }
        
        //Pose
        if (player.isSneaking()) {
            if (this.getPose() >= 12) {
                this.setPose(0);
            } else {
                this.setPose(this.getPose() + 1);
            }
            return false; // Returning true would consume the item
        }
        
        //Inventory
        boolean isArmor;
        
        boolean hasItemInHand = !item.isNull();
        int slot;
        
        if (hasItemInHand && item instanceof ItemArmor) {
            ItemArmor itemArmor = (ItemArmor) item;
            isArmor = true;
            slot = getArmorSlot(itemArmor);
        } else if (hasItemInHand && (item.getId() == ItemID.SKULL) || item.getBlockId() == BlockID.CARVED_PUMPKIN) {
            isArmor = true;
            slot = EntityArmorInventory.SLOT_HEAD;
        } else if (hasItemInHand) {
            isArmor = false;
            if (item.getId() == ItemID.SHIELD) {
                slot = EntityEquipmentInventory.OFFHAND;
            } else {
                slot = EntityEquipmentInventory.MAIN_HAND;
            }
        } else {
            double clickHeight = clickedPos.y - this.y;
            if (clickHeight >= 0.1 && clickHeight < 0.55 && !armorInventory.getBoots().isNull()) {
                isArmor = true;
                slot = EntityArmorInventory.SLOT_FEET;
            } else if (clickHeight >= 0.9 && clickHeight < 1.6) {
                if (!equipmentInventory.getItemInOffhand().isNull()) {
                    isArmor = false;
                    slot = EntityEquipmentInventory.OFFHAND;
                } else if (!equipmentInventory.getItemInHand().isNull()) {
                    isArmor = false;
                    slot = EntityEquipmentInventory.MAIN_HAND;
                } else if (!armorInventory.getChestplate().isNull()) {
                    isArmor = true;
                    slot = EntityArmorInventory.SLOT_CHEST;
                } else {
                    return false;
                }
            } else if (clickHeight >= 0.4 && clickHeight < 1.2 && !armorInventory.getLeggings().isNull()) {
                isArmor = true;
                slot = EntityArmorInventory.SLOT_LEGS;
            } else if (clickHeight >= 1.6 && !armorInventory.getHelmet().isNull()) {
                isArmor = true;
                slot = EntityArmorInventory.SLOT_HEAD;
            } else if (!equipmentInventory.getItemInOffhand().isNull()) {
                isArmor = false;
                slot = EntityEquipmentInventory.OFFHAND;
            } else if (!equipmentInventory.getItemInHand().isNull()) {
                isArmor = false;
                slot = EntityEquipmentInventory.MAIN_HAND;
            } else {
                return false;
            }
        }

        boolean changed = false;
        if (isArmor) {
            changed = this.tryChangeEquipment(player, item, slot, true);
            slot = EntityEquipmentInventory.MAIN_HAND;
        }
        
        if (!changed) {
            changed = this.tryChangeEquipment(player, item, slot, false);
        }
        
        if (changed) {
            level.addSound(this, Sound.MOB_ARMOR_STAND_PLACE);
        }

        return false; // Returning true would consume the item but tryChangeEquipment already manages the inventory
    }

    private boolean tryChangeEquipment(Player player, Item handItem, int slot, boolean isArmorSlot) {
        BaseInventory inventory = isArmorSlot? armorInventory : equipmentInventory;
        Item item = inventory.getItem(slot);
        
        if (item.isNull() && !handItem.isNull()) {
            // Adding item to the armor stand
            Item itemClone = handItem.clone();
            itemClone.setCount(1);
            inventory.setItem(slot, itemClone);
            if (!player.isCreative()) {
                handItem.count--;
                player.getInventory().setItem(player.getInventory().getHeldItemIndex(), handItem);
            }
            return true;
        } else if (!item.isNull()) {
            Item itemtoAddToArmorStand = Item.getBlock(BlockID.AIR);
            if (!handItem.isNull()) {
                if (handItem.equals(item, true, true)) {
                    // Attempted to replace with the same item type
                    return false;
                }
                
                if (item.count > 1) {
                    // The armor stand have more items than 1, item swapping is not supported in this situation
                    return false;
                }
                
                Item itemToSetToPlayerInv;
                if (handItem.count > 1) {
                    itemtoAddToArmorStand = handItem.clone();
                    itemtoAddToArmorStand.setCount(1);
                    
                    itemToSetToPlayerInv = handItem.clone();
                    itemToSetToPlayerInv.count--;
                } else {
                    itemtoAddToArmorStand = handItem.clone();
                    itemToSetToPlayerInv = Item.getBlock(BlockID.AIR);
                }
                player.getInventory().setItem(player.getInventory().getHeldItemIndex(), itemToSetToPlayerInv);
            }
            
            // Removing item from the armor stand
            Item[] notAdded = player.getInventory().addItem(item);
            if (notAdded.length > 0) {
                if (notAdded[0].count == item.count) {
                    if (!handItem.isNull()) {
                        player.getInventory().setItem(player.getInventory().getHeldItemIndex(), handItem);
                    }
                    return false;
                }
                
                Item itemClone = item.clone();
                itemClone.count -= notAdded[0].count;
                inventory.setItem(slot, itemClone);
            } else {
                inventory.setItem(slot, itemtoAddToArmorStand);
            }
            return true;
        }
        return false;
    }


    private int getPose() {
        return this.dataProperties.getInt(Entity.DATA_ARMOR_STAND_POSE_INDEX);
    }

    private void setPose(int pose) {
        this.dataProperties.putInt(Entity.DATA_ARMOR_STAND_POSE_INDEX, pose);
        SetEntityDataPacket setEntityDataPacket = new SetEntityDataPacket();
        setEntityDataPacket.eid = this.getId();
        setEntityDataPacket.metadata = this.getDataProperties();
        Server.getInstance().getOnlinePlayers().values().forEach(all -> all.dataPacket(setEntityDataPacket));
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.put(TAG_MAINHAND, NBTIO.putItemHelper(this.equipmentInventory.getItemInHand()));
        this.namedTag.put(TAG_OFFHAND, NBTIO.putItemHelper(this.equipmentInventory.getItemInOffhand()));

        if (this.armorInventory != null) {
            ListTag<CompoundTag> armorTag = new ListTag<>(TAG_ARMOR);
            for (int i = 0; i < 4; i++) {
                armorTag.add(NBTIO.putItemHelper(this.armorInventory.getItem(i), i));
            }
            this.namedTag.putList(armorTag);
        }

        this.namedTag.putInt(TAG_POSE_INDEX, this.getPose());
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        this.equipmentInventory.sendContents(player);
        this.armorInventory.sendContents(player);
    }

    @Override
    public void spawnToAll() {
        if (this.chunk != null && !this.closed) {
            Collection<Player> chunkPlayers = this.level.getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values();
            for (Player chunkPlayer : chunkPlayers) {
                this.spawnTo(chunkPlayer);
            }
        }
    }

    @Override
    public void fall(float fallDistance) {
        super.fall(fallDistance);

        this.getLevel().addSound(this, Sound.MOB_ARMOR_STAND_LAND);
    }

    @Override
    public void kill() {
        super.kill();
        EntityDamageEvent lastDamageCause = this.lastDamageCause;
        boolean byAttack = lastDamageCause != null && lastDamageCause.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK;
        
        Vector3 pos = getPosition();
        
        pos.y += 0.2;
        level.dropItem(pos, armorInventory.getBoots());
        
        pos.y = y + 0.6;
        level.dropItem(pos, armorInventory.getLeggings());
        
        pos.y = y + 1.4;
        level.dropItem(byAttack? pos : this, Item.get(ItemID.ARMOR_STAND));
        level.dropItem(pos, armorInventory.getChestplate());
        equipmentInventory.getContents().values().forEach(items -> this.level.dropItem(this, items));
        equipmentInventory.clearAll();
        
        pos.y = y + 1.8;
        level.dropItem(pos, armorInventory.getHelmet());
        armorInventory.clearAll();

        level.addSound(this, Sound.MOB_ARMOR_STAND_BREAK);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {

        switch (source.getCause()) {
            case FALL:
                source.setCancelled(true);
                level.addSound(this, Sound.MOB_ARMOR_STAND_LAND);
                break;
            case CONTACT:
            case HUNGER:
            case MAGIC:
            case DROWNING:
            case SUFFOCATION:
            case PROJECTILE:
                source.setCancelled(true);
                break;
            case FIRE:
            case FIRE_TICK:
            case LAVA:
                if (hasEffect(Effect.FIRE_RESISTANCE)) {
                    return false;
                }
            default:
        }
        
        if (source.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            if (namedTag.getByte("InvulnerableTimer") > 0) {
                source.setCancelled(true);
            }
            if (super.attack(source)) {
                namedTag.putByte("InvulnerableTimer", 9);
                return true;
            }
            return false;
        }
        
        getServer().getPluginManager().callEvent(source);
        if (source.isCancelled()) {
            return false;
        }
        setLastDamageCause(source);

        if (getDataPropertyInt(DATA_HURT_TIME) > 0) {
            setHealth(0);
            return true;
        }
        
        if (source instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) source;
            if (event.getDamager() instanceof Player){
                Player player = (Player) event.getDamager();
                if (player.isCreative()) {
                    this.level.addParticle(new DestroyBlockParticle(this, Block.get(BlockID.WOODEN_PLANKS)));
                    this.close();
                    return true;
                }
            }
        }

        setDataProperty(new IntEntityData(DATA_HURT_TIME, 9), true);
        level.addSound(this, Sound.MOB_ARMOR_STAND_HIT);
        
        return true;
    }

    @Nonnull
    @Override
    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : "Armor Stand";
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean hasUpdate = super.entityBaseTick(tickDiff);

        int hurtTime = getDataPropertyInt(DATA_HURT_TIME);
        if (hurtTime > 0 && age % 2 == 0) {
            setDataProperty(new IntEntityData(DATA_HURT_TIME, hurtTime - 1), true);
            hasUpdate = true;
        }
        hurtTime = namedTag.getByte("InvulnerableTimer");
        if (hurtTime > 0 && age % 2 == 0) {
            namedTag.putByte("InvulnerableTimer", hurtTime - 1);
        }

        return hasUpdate;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public EntityEquipmentInventory getEquipmentInventory() {
        return this.equipmentInventory;
    }

    @Override
    public EntityArmorInventory getInventory() {
        return this.armorInventory;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        int tickDiff = currentTick - lastUpdate;
        boolean hasUpdated = super.onUpdate(currentTick);

        if (closed || tickDiff <= 0 && !justCreated) {
            return hasUpdated;
        }

        this.timing.startTiming();

        lastUpdate = currentTick;

        boolean hasUpdate = entityBaseTick(tickDiff);

        if (isAlive()) {
            if (getHealth() < getMaxHealth()) {
                setHealth(getHealth() + 0.001f);
            }
            motionY -= getGravity();
            
            double highestPosition = this.highestPosition;
            move(motionX, motionY, motionZ);

            float friction = 1 - getDrag();

            motionX *= friction;
            motionY *= 1 - getDrag();
            motionZ *= friction;

            updateMovement();
            hasUpdate = true;
            if (onGround && (highestPosition - y) >= 3) {
                level.addSound(this, Sound.MOB_ARMOR_STAND_LAND);
            }
        }

        this.timing.stopTiming();

        return hasUpdate || !onGround || Math.abs(motionX) > 0.00001 || Math.abs(motionY) > 0.00001 || Math.abs(motionZ) > 0.00001;
    }

    @Override
    public String getInteractButtonText() {
        return "action.interact.armorstand.equip";
    }

    @Override
    public boolean canDoInteraction() {
        return true;
    }
}

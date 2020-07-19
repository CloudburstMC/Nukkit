package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.EntityArmorInventory;
import cn.nukkit.inventory.EntityEquipmentInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.item.ItemArmorStand;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.SetEntityDataPacket;
import cn.nukkit.scheduler.Task;

import java.util.Collection;

public class EntityArmorStand extends EntityLiving implements InventoryHolder {

    private EntityEquipmentInventory equipmentInventory;
    private EntityArmorInventory armorInventory;

    private static final String TAG_MAINHAND = "Mainhand";
    private static final String TAG_POSE_INDEX = "PoseIndex";
    private static final String TAG_OFFHAND = "Offhand";
    private static final String TAG_ARMOR = "Armor";

    private static final int DATA_FLAGS2 = 91; //long (extended data flags)
    private int vibrateTimer = 0;

    @Override
    public int getNetworkId() {
        return 61;
    }

    public EntityArmorStand(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(6);
        this.setHealth(6);
        this.setImmobile(true);

        super.initEntity();

        this.equipmentInventory = new EntityEquipmentInventory(this);
        this.armorInventory = new EntityArmorInventory(this);

        if (this.namedTag.contains(TAG_MAINHAND)) {
            this.equipmentInventory.setItemInHand(NBTIO.getItemHelper(this.namedTag.getCompound(TAG_MAINHAND)), true);
        }

        if (this.namedTag.contains(TAG_OFFHAND)) {
            this.equipmentInventory.setOffhandItem(NBTIO.getItemHelper(this.namedTag.getCompound(TAG_OFFHAND)), true);
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
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        //Pose
        if (player.isSneaking()) {
            if (this.getPose() >= 12) {
                this.setPose(0);
            } else {
                this.setPose(this.getPose() + 1);
            }
            return true;
        }
        //Inventory
        if (this.isValid() && !player.isSpectator()) {
            int i = 0;
            boolean flag = !item.isNull();
            boolean isArmorSlot = false;

            if (flag && item instanceof ItemArmor) {
                ItemArmor itemArmor = (ItemArmor) item;
                i = getArmorSlot(itemArmor);
                isArmorSlot = true;
            }

            if (flag && (item.getId() == ItemID.SKULL) || item.getBlockId() == BlockID.PUMPKIN) {
                i = 0;
            }

            int j =0;
            double d3 = clickedPos.y - this.y;
            boolean flag2 = false;

            if (d3 >= 0.1 && d3 < 0.55 && !this.armorInventory.getItem(EntityArmorInventory.SLOT_FEET).isNull()) {
                j = 3;
                flag2 = isArmorSlot = true;
            } else if (d3 >= 0.9 && d3 < 1.6 && !this.armorInventory.getItem(EntityArmorInventory.SLOT_CHEST).isNull()) {
                j = 1;
                flag2 = isArmorSlot = true;
            } else if (d3 >= 0.4 && d3 < 1.2 && !this.armorInventory.getItem(EntityArmorInventory.SLOT_LEGS).isNull()) {
                j = 2;
                flag2 = isArmorSlot = true;
            } else if (d3 >= 1.6 && !this.armorInventory.getItem(EntityArmorInventory.SLOT_HEAD).isNull()) {
                flag2 = isArmorSlot = true;
            } else if (!this.equipmentInventory.getItem(j).isNull()) {
                flag2 = true;
            }

            if (flag) {
                this.tryChangeEquipment(player, item, i, isArmorSlot);
            } else if (flag2) {
                this.tryChangeEquipment(player, item, j, isArmorSlot);
            }

            this.equipmentInventory.sendContents(player);
            this.armorInventory.sendContents(player);
            return flag || flag2;
        }
        return false;
    }

    private void tryChangeEquipment(Player player, Item handItem, int slot, boolean isArmorSlot) {
        Item item = isArmorSlot ? this.armorInventory.getItem(slot) : this.equipmentInventory.getItem(slot);

        if (player.isCreative() && item.isNull() && !handItem.isNull()) {
            Item itemClone = handItem.clone();
            itemClone.setCount(1);
            if (isArmorSlot) {
                this.armorInventory.setItem(slot, itemClone);
            } else {
                this.equipmentInventory.setItem(slot, itemClone);
            }
        } else if (!handItem.isNull() && handItem.getCount() > 1) {
            if (item.isNull()) {
                Item itemClone = handItem.clone();
                itemClone.setCount(1);
                if (isArmorSlot) {
                    this.armorInventory.setItem(slot, itemClone);
                } else {
                    this.equipmentInventory.setItem(slot, itemClone);
                }
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            }
        } else {
            if (isArmorSlot) {
                this.armorInventory.setItem(slot, handItem);
            } else {
                this.equipmentInventory.setItem(slot, handItem);
            }
            int index = player.getInventory().getHeldItemIndex();
            Server.getInstance().getScheduler().scheduleDelayedTask(new Task() {
                @Override
                public void onRun(int i) {
                    player.getInventory().decreaseCount(index);
                    player.getInventory().addItem(item);
                }
            }, 1);
        }

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
        this.namedTag.put(TAG_OFFHAND, NBTIO.putItemHelper(this.equipmentInventory.getOffHandItem()));

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
        if(this.chunk != null && !this.closed){
            Collection<Player> chunkPlayers = this.level.getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values();
            for (Player chunkPlayer : chunkPlayers) {
                this.spawnTo(chunkPlayer);
            }
        }
    }

    @Override
    public void fall(float fallDistance) {
        super.fall(fallDistance);

        this.level.addLevelSoundEvent(this, LevelEventPacket.EVENT_SOUND_ARMOR_STAND_FALL);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) source;
            Entity damager = entityDamageByEntityEvent.getDamager();
            if (damager instanceof Player) {
                Player damagerPlayer = (Player) damager;
                if (damagerPlayer.isCreative()) {
                    this.level.addParticle(new DestroyBlockParticle(this, Block.get(BlockID.WOODEN_PLANKS)));
                    this.close();
                    return true;
                } else {
                    if (level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                        this.level.dropItem(this, new ItemArmorStand());
                        this.equipmentInventory.getContents().values().forEach(items -> this.level.dropItem(this, items));
                        this.equipmentInventory.clearAll();
                        this.armorInventory.getContents().values().forEach(items -> this.level.dropItem(this, items));
                        this.armorInventory.clearAll();
                    }
                }
            }

        }
        
        switch (source.getCause()) {
            case CONTACT:
            case FALL:
                source.setCancelled(true);
                break;
            default:
        }

        super.attack(source);

        if (!source.isCancelled()) {
            this.level.addParticle(new DestroyBlockParticle(this, Block.get(BlockID.WOODEN_PLANKS)));
            this.setGenericFlag(Entity.DATA_FLAG_VIBRATING, true);
            this.vibrateTimer = 20;
            this.close();
        }

        return false;
    }

    @Override
    public String getName() {
        return "ArmorStand";
    }

    private static int getArmorSlot(ItemArmor armorItem) {
        if (armorItem.isHelmet()) {
            return 0;
        } else if (armorItem.isChestplate()) {
            return 1;
        } else if (armorItem.isLeggings()) {
            return 2;
        } else {
            return 3;
        }
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean hasUpdate = super.entityBaseTick(tickDiff);

        if (this.getGenericFlag(Entity.DATA_FLAG_VIBRATING) && this.vibrateTimer-- <= 0) {
            this.setGenericFlag(Entity.DATA_FLAG_VIBRATING, false);
        }

        return hasUpdate;
    }


    private void setGenericFlag(int propertyId, boolean value) {
        this.setDataFlag(propertyId >= 64 ? DATA_FLAGS2 : Entity.DATA_FLAGS, propertyId % 64, value);
    }

    private boolean getGenericFlag(int propertyId) {
        return this.getDataFlag(propertyId >= 64 ? DATA_FLAGS2 : Entity.DATA_FLAGS, propertyId % 64);
    }

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

        if (isAlive() && !onGround) {
            motionY -= getGravity();

            move(motionX, motionY, motionZ);

            float friction = 1 - getDrag();

            motionX *= friction;
            motionY *= 1 - getDrag();
            motionZ *= friction;
            
            updateMovement();
            hasUpdate = true;
        }

        this.timing.stopTiming();

        return hasUpdate || !onGround || Math.abs(motionX) > 0.00001 || Math.abs(motionY) > 0.00001 || Math.abs(motionZ) > 0.00001;
    }
}

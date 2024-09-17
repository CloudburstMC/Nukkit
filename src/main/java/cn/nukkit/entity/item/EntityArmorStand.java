package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.EntityArmorInventory;
import cn.nukkit.inventory.EntityEquipmentInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.Collection;

public class EntityArmorStand extends Entity implements InventoryHolder, EntityInteractable {

    public static final int NETWORK_ID = 61;

    public static final String TAG_MAINHAND = "Mainhand";
    public static final String TAG_OFFHAND = "Offhand";
    public static final String TAG_POSE_INDEX = "PoseIndex";
    public static final String TAG_ARMOR = "Armor";

    private EntityEquipmentInventory equipmentInventory;
    private EntityArmorInventory armorInventory;

    private int pose;
    private String nameTag;

    public EntityArmorStand(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.updateMode = 3;

        if (nbt.contains(TAG_POSE_INDEX)) {
            this.setPose(nbt.getInt(TAG_POSE_INDEX));
        }
    }

    private static int getArmorSlot(ItemArmor armorItem) {
        if (armorItem.canBePutInHelmetSlot()) {
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
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected float getGravity() {
        return 0.04f;
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
    protected void initEntity() {
        this.setMaxHealth(6);

        super.initEntity();

        this.setHealth(6);
        this.setImmobile(true);

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
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (!this.isAlive()) {
            return false;
        }

        if (item.getId() == Item.NAME_TAG && !player.isAdventure()) {
            if (item.hasCustomName()) {
                String name = item.getCustomName();
                this.namedTag.putString("CustomName", name);
                this.namedTag.putBoolean("CustomNameVisible", true);
                this.setNameTag(name);
                this.setNameTagVisible(true);
                return true; // onInteract: true = decrease count
            }
        }

        if (player.sneakToBlockInteract()) {
            if (this.getPose() >= 12) {
                this.setPose(0);
            } else {
                this.setPose(this.getPose() + 1);
            }
            this.sendData(this.getViewers().values().toArray(new Player[0]));
            return false; // do not consume item
        }

        if (this.isValid() && !player.isSpectator()) {
            int i = 0;
            boolean flag = !item.isNull();
            boolean isArmorSlot = false;

            if (flag && item instanceof ItemArmor) {
                ItemArmor itemArmor = (ItemArmor) item;
                i = getArmorSlot(itemArmor);
                isArmorSlot = true;
            }

            if (flag && (item.getId() == Item.SKULL) || item.getId() == (255 - BlockID.CARVED_PUMPKIN)) {
                i = 0;
                isArmorSlot = true;
            }

            int j = 0;
            double d3 = clickedPos.y - this.y;
            boolean flag2 = false;

            if (d3 >= 0.1 && d3 < 0.55 && !this.armorInventory.getItemFast(EntityArmorInventory.SLOT_FEET).isNull()) {
                j = 3;
                flag2 = isArmorSlot = true;
            } else if (d3 >= 0.9 && d3 < 1.6 && !this.armorInventory.getItemFast(EntityArmorInventory.SLOT_CHEST).isNull()) {
                j = 1;
                flag2 = isArmorSlot = true;
            } else if (d3 >= 0.4 && d3 < 1.2 && !this.armorInventory.getItemFast(EntityArmorInventory.SLOT_LEGS).isNull()) {
                j = 2;
                flag2 = isArmorSlot = true;
            } else if (d3 >= 1.6 && !this.armorInventory.getItemFast(EntityArmorInventory.SLOT_HEAD).isNull()) {
                flag2 = isArmorSlot = true;
            } else if (!this.equipmentInventory.getItemFast(j).isNull()) {
                flag2 = true;
            }

            if (flag) {
                this.tryChangeEquipment(player, item, i, isArmorSlot);
            } else if (flag2) {
                this.tryChangeEquipment(player, item, j, isArmorSlot);
            }
            return false; // Item set in tryChangeEquipment
        }
        return false;
    }

    private void tryChangeEquipment(Player player, Item newItem, int slot, boolean isArmorSlot) {
        Item currentItem = isArmorSlot ? this.armorInventory.getItem(slot) : this.equipmentInventory.getItem(slot);

        if (currentItem.equals(newItem)) {
            return;
        }

        if (newItem.isNull()) {
            if (isArmorSlot) {
                this.armorInventory.setItem(slot, Item.get(Item.AIR));
            } else {
                this.equipmentInventory.setItem(slot, Item.get(Item.AIR));
            }
        } else {
            if (!player.isCreative()) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            }

            Item itemToAdd = newItem.clone();
            itemToAdd.setCount(1);
            if (isArmorSlot) {
                this.armorInventory.setItem(slot, itemToAdd);
            } else {
                this.equipmentInventory.setItem(slot, itemToAdd);
            }
        }

        if (!currentItem.isNull()) {
            player.getInventory().addItem(currentItem);
        }

        Collection<Player> viewers = this.getViewers().values();
        this.equipmentInventory.sendContents(viewers);
        this.armorInventory.sendContents(viewers);
    }

    public int getPose() {
        return this.pose;
    }

    public void setPose(int pose) {
        this.pose = pose;
        this.dataProperties.putInt(Entity.DATA_ARMOR_STAND_POSE_INDEX, pose);
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
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == EntityDamageEvent.DamageCause.CONTACT) {
            source.setCancelled(true);
        }

        if (!this.isAlive() || !super.attack(source)) {
            return false;
        }

        if (!source.isCancelled() && !this.closed) {
            this.setGenericFlag(Entity.DATA_FLAG_VIBRATING, true);
            this.level.addParticle(new DestroyBlockParticle(this, Block.get(Block.WOODEN_PLANKS)));
            this.kill(); // Using close() here would not leave any time for the vibrating effect to display
            if (source instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) source;
                if (event.getDamager() instanceof Player) {
                    Player player = (Player) event.getDamager();
                    if (player.isCreative()) {
                        this.close();
                        return true;
                    }

                    boolean drop = this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS);
                    if (drop) {
                        this.level.dropItem(this, Item.get(Item.ARMOR_STAND));
                    }
                    if (this.equipmentInventory != null) {
                        if (drop) {
                            this.equipmentInventory.getContents().values().forEach(items -> this.level.dropItem(this, items));
                        }
                        this.equipmentInventory.clearAll();
                    }
                    if (this.armorInventory != null) {
                        if (drop) {
                            this.armorInventory.getContents().values().forEach(items -> this.level.dropItem(this, items));
                        }
                        this.armorInventory.clearAll();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : "Armor Stand";
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
        if (this.closed) {
            return false;
        }

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.updateMode % 2 == 1) {
            this.updateMode = 3;

            if (this.onGround) {
                if (level.getBlockIdAt(chunk, getFloorX(), getFloorY() - 1, getFloorZ()) == 0) {
                    this.onGround = false;
                }
            }
        }

        if (!this.onGround) {
            this.motionY -= getGravity();
        } else {
            this.motionY = 0;
        }

        this.move(this.motionX, this.motionY, this.motionZ);

        this.motionX *= 0.9;
        this.motionY *= 0.9;
        this.motionZ *= 0.9;

        this.updateMovement();

        return hasUpdate || !(this.motionX == 0 && this.motionY == 0 && this.motionZ == 0);
    }

    @Override
    public String getInteractButtonText() {
        return "action.interact.armorstand.equip";
    }

    @Override
    public boolean canDoInteraction() {
        return true;
    }

    @Override
    public void setNameTag(String name) {
        this.nameTag = name;
        if (this.namedTag.contains("CustomNameVisible") || this.namedTag.contains("CustomNameAlwaysVisible")) { // Hack: Vanilla: Disable client side name tag while keeping custom name in nbt
            this.setDataProperty(new StringEntityData(DATA_NAMETAG, name));
        }
    }

    @Override
    public boolean hasCustomName() {
        return this.nameTag != null;
    }

    @Override
    public String getNameTag() {
        return this.nameTag == null ? "" : this.nameTag;
    }
}

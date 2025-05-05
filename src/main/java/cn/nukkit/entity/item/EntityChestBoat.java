package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.inventory.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

public class EntityChestBoat extends EntityBoat implements InventoryHolder {

    public static final int NETWORK_ID = 218;

    protected ChestBoatInventory inventory;

    public EntityChestBoat(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public String getName() {
        return "Boat with Chest";
    }

    @Override
    public String getInteractButtonText(Player player) {
        if (player.sneakToBlockInteract()) {
            return "action.interact.opencontainer";
        }
        return super.getInteractButtonText();
    }

    @Override
    public boolean isFull() {
        return !this.passengers.isEmpty();
    }

    @Override
    public ChestBoatInventory getInventory() {
        if (this.inventory == null) {
            this.initInventory();
        }
        return this.inventory;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (player.sneakToBlockInteract() && this.isAlive()) {
            player.addWindow(this.getInventory());
            return false;
        }

        return super.onInteract(player, item, clickedPos);
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.dataProperties
                .putByte(DATA_CONTAINER_TYPE, InventoryType.CHEST_BOAT.getNetworkType())
                .putInt(DATA_CONTAINER_BASE_SIZE, 27)
                .putInt(DATA_CONTAINER_EXTRA_SLOTS_PER_STRENGTH, 0);
    }

    private void initInventory() {
        if (!this.namedTag.contains("Items") || !(this.namedTag.get("Items") instanceof ListTag)) {
            this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        }
        ListTag<CompoundTag> list = (ListTag<CompoundTag>) this.namedTag.getList("Items");

        this.inventory = new ChestBoatInventory(this);

        for (CompoundTag compound : list.getAll()) {
            Item item = NBTIO.getItemHelper(compound);
            if (item.getId() != 0 && item.getCount() > 0) {
                this.inventory.slots.put(compound.getByte("Slot"), item);
            }
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (this.inventory != null) {
            this.namedTag.putList(new ListTag<CompoundTag>("Items"));
            for (int slot = 0; slot < 27; ++slot) {
                Item item = this.inventory.getItem(slot);
                if (item != null && item.getId() != Item.AIR) {
                    this.namedTag.getList("Items", CompoundTag.class)
                            .add(NBTIO.putItemHelper(item, slot));
                }
            }
        }
    }

    @Override
    protected void dropItem() {
        switch (this.getVariant()) {
            case 0:
                this.level.dropItem(this, Item.get(ItemID.OAK_CHEST_BOAT));
                break;
            case 1:
                this.level.dropItem(this, Item.get(ItemID.SPRUCE_CHEST_BOAT));
                break;
            case 2:
                this.level.dropItem(this, Item.get(ItemID.BIRCH_CHEST_BOAT));
                break;
            case 3:
                this.level.dropItem(this, Item.get(ItemID.JUNGLE_CHEST_BOAT));
                break;
            case 4:
                this.level.dropItem(this, Item.get(ItemID.ACACIA_CHEST_BOAT));
                break;
            case 5:
                this.level.dropItem(this, Item.get(ItemID.DARK_OAK_CHEST_BOAT));
                break;
            case 6:
                this.level.dropItem(this, Item.get(ItemID.MANGROVE_CHEST_BOAT));
                break;
            case 7:
                this.level.dropItem(this, Item.get(ItemID.BAMBOO_CHEST_RAFT));
                break;
            case 8:
                this.level.dropItem(this, Item.get(ItemID.CHERRY_CHEST_BOAT));
                break;
            case 9:
                this.level.dropItem(this, Item.get(ItemID.PALE_OAK_CHEST_BOAT));
                break;
        }

        if (this.inventory == null) {
            this.initInventory();
        }
        if (this.inventory != null) {
            this.inventory.getViewers().clear();
            for (Item item : this.inventory.getContents().values()) {
                this.level.dropItem(this, item);
            }
            this.inventory.clearAll();
        }
    }
}

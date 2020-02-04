package cn.nukkit.blockentity;

import cn.nukkit.block.BlockIds;
import cn.nukkit.inventory.BarrelInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.player.Player;

import java.util.HashSet;

import static cn.nukkit.block.BlockIds.AIR;

public class BlockEntityBarrel extends BlockEntitySpawnable implements InventoryHolder, BlockEntityContainer, BlockEntityNameable {

    private BarrelInventory inventory;

    public BlockEntityBarrel(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        this.inventory = new BarrelInventory(this);

        if (!this.namedTag.contains("Items") || !(this.namedTag.get("Items") instanceof ListTag)) {
            this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        }

        ListTag<CompoundTag> list = (ListTag<CompoundTag>) this.namedTag.getList("Items");
        for (CompoundTag compound : list.getAll()) {
            Item item = NBTIO.getItemHelper(compound);
            this.inventory.slots.put(compound.getByte("Slot"), item);
        }

        super.initBlockEntity();
    }

    @Override
    public void close() {
        if (!closed) {
            for (Player player : new HashSet<>(this.getInventory().getViewers())) {
                player.removeWindow(this.getInventory());
            }
            super.close();
        }
    }

    @Override
    public void onBreak() {
        for (Item content : this.inventory.getContents().values()) {
            this.level.dropItem(this, content);
        }
        this.inventory.clearAll(); // Stop items from being moved around by another player in the inventory
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        for (int index = 0; index < this.getSize(); index++) {
            this.setItem(index, this.inventory.getItem(index));
        }
    }

    @Override
    public Item getItem(int index) {
        int i = this.getSlotIndex(index);
        if (i < 0) {
            return Item.get(AIR, 0, 0);
        } else {
            CompoundTag data = (CompoundTag) this.namedTag.getList("Items").get(i);
            return NBTIO.getItemHelper(data);
        }
    }

    @Override
    public void setItem(int index, Item item) {
        int i = this.getSlotIndex(index);

        CompoundTag d = NBTIO.putItemHelper(item, index);

        // If item is air or count less than 0, remove the item from the "Items" list
        if (item.getId() == AIR || item.getCount() <= 0) {
            if (i >= 0) {
                this.namedTag.getList("Items").remove(i);
            }
        } else if (i < 0) {
            // If it is less than i, then it is a new item, so we are going to add it at the end of the list
            (this.namedTag.getList("Items", CompoundTag.class)).add(d);
        } else {
            // If it is more than i, then it is an update on a inventorySlot, so we are going to overwrite the item in the list
            (this.namedTag.getList("Items", CompoundTag.class)).add(i, d);
        }
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.BARREL)
                .putInt("x", this.x)
                .putInt("y", this.y)
                .putInt("z", this.z)
                .putBoolean("isMovable", this.isMovable())
                .putBoolean("Findable", false);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == BlockIds.BARREL;
    }

    @Override
    public BarrelInventory getInventory() {
        return this.inventory;
    }

    @Override
    public String getName() {
        return hasName() ? this.namedTag.getString("CustomName") : "Barrel";
    }

    @Override
    public boolean hasName() {
        return this.namedTag.contains("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.equals("")) {
            this.namedTag.remove("CustomName");
            return;
        }

        this.namedTag.putString("CustomName", name);
    }

    protected int getSlotIndex(int index) {
        ListTag<CompoundTag> list = this.namedTag.getList("Items", CompoundTag.class);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getByte("Slot") == index) {
                return i;
            }
        }

        return -1;
    }
}
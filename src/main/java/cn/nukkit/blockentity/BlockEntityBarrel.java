package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.BarrelInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.ArrayList;

public class BlockEntityBarrel extends BlockEntitySpawnable implements InventoryHolder, BlockEntityContainer, BlockEntityNameable {

    protected BarrelInventory inventory;

    public BlockEntityBarrel(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    private void initInventory() {
        if (!this.namedTag.contains("Items") || !(this.namedTag.get("Items") instanceof ListTag)) {
            this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        }
        ListTag<CompoundTag> list = (ListTag<CompoundTag>) this.namedTag.getList("Items");

        this.inventory = new BarrelInventory(this);

        for (CompoundTag compound : list.getAll()) {
            Item item = NBTIO.getItemHelper(compound);
            if (item.getId() != 0 && item.getCount() > 0) {
                this.inventory.slots.put(compound.getByte("Slot"), item);
            }
        }
    }

    @Override
    public void close() {
        if (!this.closed && this.inventory != null) {
            for (Player player : new ArrayList<>(this.inventory.getViewers())) {
                player.removeWindow(this.inventory);
            }
        }

        super.close();
    }

    @Override
    public void onBreak() {
        if (this.inventory == null) {
            this.initInventory();
        }
        for (Item content : inventory.getContents().values()) {
            level.dropItem(this, content);
        }
        inventory.clearAll(); // Stop items from being moved around by another player in the inventory
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (this.inventory != null) {
            this.namedTag.putList(new ListTag<CompoundTag>("Items"));
            for (int index = 0; index < this.getSize(); index++) {
                this.setItem(index, this.inventory.getItem(index));
            }
        }
    }

    @Override
    public boolean isBlockEntityValid() {
        return level.getBlockIdAt(chunk, (int) x, (int) y, (int) z) == BlockID.BARREL;
    }

    @Override
    public int getSize() {
        return 27;
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

    @Override
    public Item getItem(int index) {
        int i = this.getSlotIndex(index);
        if (i < 0) {
            return new ItemBlock(Block.get(BlockID.AIR), 0, 0);
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
        if (item.getId() == Item.AIR || item.getCount() <= 0) {
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
    public BarrelInventory getInventory() {
        if (this.inventory == null) {
            this.initInventory();
        }
        return this.inventory;
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Barrel";
    }

    @Override
    public boolean hasName() {
        return this.namedTag.contains("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            this.namedTag.remove("CustomName");
            return;
        }

        this.namedTag.putString("CustomName", name);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag c = new CompoundTag()
                .putString("id", BlockEntity.BARREL)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        if (this.hasName()) {
            c.put("CustomName", this.namedTag.get("CustomName"));
        }

        return c;
    }
}

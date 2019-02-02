package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.ShulkerBoxInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.HashSet;

/**
 * Created by PetteriM1
 */
public class BlockEntityShulkerBox extends BlockEntitySpawnable implements InventoryHolder, BlockEntityContainer, BlockEntityNameable {

    protected final ShulkerBoxInventory inventory;

    public BlockEntityShulkerBox(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.inventory = new ShulkerBoxInventory(this);

        if (!this.namedTag.contains("Items") || !(this.namedTag.get("Items") instanceof ListTag)) {
            this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        }

        ListTag<CompoundTag> list = (ListTag<CompoundTag>) this.namedTag.getList("Items");
        for (CompoundTag compound : list.getAll()) {
            Item item = NBTIO.getItemHelper(compound);
            this.inventory.slots.put(compound.getByte("Slot"), item);
        }
    }

    @Override
    public void close() {
        if (!closed) {
            for (Player player : new HashSet<>(this.getInventory().getViewers())) {
                player.removeWindow(this.getInventory());
            }

            for (Player player : new HashSet<>(this.getInventory().getViewers())) {
                player.removeWindow(this.getRealInventory());
            }
            super.close();
        }
    }

    @Override
    public void saveNBT() {
        this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        for (int index = 0; index < this.getSize(); index++) {
            this.setItem(index, this.inventory.getItem(index));
        }
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == Block.SHULKER_BOX;
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
            return new ItemBlock(new BlockAir(), 0, 0);
        } else {
            CompoundTag data = (CompoundTag) this.namedTag.getList("Items").get(i);
            return NBTIO.getItemHelper(data);
        }
    }

    @Override
    public void setItem(int index, Item item) {
        int i = this.getSlotIndex(index);

        CompoundTag d = NBTIO.putItemHelper(item, index);

        if (item.getId() == Item.AIR || item.getCount() <= 0) {
            if (i >= 0) {
                this.namedTag.getList("Items").remove(i);
            }
        } else if (i < 0) {
            (this.namedTag.getList("Items", CompoundTag.class)).add(d);
        } else {
            (this.namedTag.getList("Items", CompoundTag.class)).add(i, d);
        }
    }

    @Override
    public BaseInventory getInventory() {
        this.checkPairing();

        return this.inventory;
    }

    public ShulkerBoxInventory getRealInventory() {
        return inventory;
    }

    protected void checkPairing() {
        BlockEntityShulkerBox pair = this.getPair();
        if (pair != null) {
            if (!pair.isPaired()) {
                pair.createPair(this);
                pair.checkPairing();
            }

            this.namedTag.remove("pairx");
            this.namedTag.remove("pairz");
        }
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Chest";
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

    public boolean isPaired() {
        return this.namedTag.contains("pairx") && this.namedTag.contains("pairz");
    }

    public BlockEntityShulkerBox getPair() {
        if (this.isPaired()) {
            BlockEntity blockEntity = this.getLevel().getBlockEntity(new Vector3(this.namedTag.getInt("pairx"), this.y, this.namedTag.getInt("pairz")));
            if (blockEntity instanceof BlockEntityShulkerBox) {
                return (BlockEntityShulkerBox) blockEntity;
            }
        }

        return null;
    }

    public boolean pairWith(BlockEntityShulkerBox chest) {
        if (this.isPaired() || chest.isPaired()) {
            return false;
        }

        this.createPair(chest);

        chest.spawnToAll();
        this.spawnToAll();
        this.checkPairing();

        return true;
    }

    public void createPair(BlockEntityShulkerBox chest) {
        this.namedTag.putInt("pairx", (int) chest.x);
        this.namedTag.putInt("pairz", (int) chest.z);
        chest.namedTag.putInt("pairx", (int) this.x);
        chest.namedTag.putInt("pairz", (int) this.z);
    }

    public boolean unpair() {
        if (!this.isPaired()) {
            return false;
        }

        BlockEntityShulkerBox chest = this.getPair();

        this.namedTag.remove("pairx");
        this.namedTag.remove("pairz");

        this.spawnToAll();

        if (chest != null) {
            chest.namedTag.remove("pairx");
            chest.namedTag.remove("pairz");
            chest.checkPairing();
            chest.spawnToAll();
        }
        this.checkPairing();

        return true;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag c;
        if (this.isPaired()) {
            c = new CompoundTag()
                    .putString("id", BlockEntity.SHULKER_BOX)
                    .putInt("x", (int) this.x)
                    .putInt("y", (int) this.y)
                    .putInt("z", (int) this.z)
                    .putInt("pairx", this.namedTag.getInt("pairx"))
                    .putInt("pairz", this.namedTag.getInt("pairz"));
        } else {
            c = new CompoundTag()
                    .putString("id", BlockEntity.SHULKER_BOX)
                    .putInt("x", (int) this.x)
                    .putInt("y", (int) this.y)
                    .putInt("z", (int) this.z);
        }

        if (this.hasName()) {
            c.put("CustomName", this.namedTag.get("CustomName"));
        }

        return c;
    }

}
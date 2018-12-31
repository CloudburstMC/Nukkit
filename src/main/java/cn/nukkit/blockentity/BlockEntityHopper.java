package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.inventory.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.HashSet;

/**
 * Created by CreeperFace on 8.5.2017.
 */
public class BlockEntityHopper extends BlockEntitySpawnable implements InventoryHolder, BlockEntityContainer, BlockEntityNameable {

    protected HopperInventory inventory;

    public int transferCooldown = 8;

    private AxisAlignedBB pickupArea;

    public BlockEntityHopper(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (this.namedTag.contains("TransferCooldown")) {
            this.transferCooldown = this.namedTag.getInt("TransferCooldown");
        }

        this.inventory = new HopperInventory(this);

        if (!this.namedTag.contains("Items") || !(this.namedTag.get("Items") instanceof ListTag)) {
            this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        }

        for (int i = 0; i < this.getSize(); i++) {
            this.inventory.setItem(i, this.getItem(i));
        }

        this.pickupArea = new SimpleAxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 2, this.z + 1);

        this.scheduleUpdate();

        super.initBlockEntity();
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.level.getBlockIdAt(this.getFloorX(), this.getFloorY(), this.getFloorZ()) == Block.HOPPER_BLOCK;
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Hopper";
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

    public boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }

    public void setTransferCooldown(int transferCooldown) {
        this.transferCooldown = transferCooldown;
    }

    @Override
    public int getSize() {
        return 5;
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
                this.namedTag.getList("Items").getAll().remove(i);
            }
        } else if (i < 0) {
            (this.namedTag.getList("Items", CompoundTag.class)).add(d);
        } else {
            (this.namedTag.getList("Items", CompoundTag.class)).add(i, d);
        }
    }

    @Override
    public void saveNBT() {
        this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        for (int index = 0; index < this.getSize(); index++) {
            this.setItem(index, this.inventory.getItem(index));
        }

        this.namedTag.putInt("TransferCooldown", this.transferCooldown);
    }

    @Override
    public HopperInventory getInventory() {
        return inventory;
    }

    @Override
    public boolean onUpdate() {
        if (this.closed) {
            return false;
        }

        this.transferCooldown--;

        if (!this.isOnTransferCooldown()) {
            boolean transfer = this.transferItemsOut();
            boolean pickup = false;

            if (!transfer){
                pickup = this.pickupDroppedItems();
            }

            if (transfer || pickup) {
                this.setTransferCooldown(8); //TODO: maybe we should update hopper every tick if nothing happens?
                setDirty();
            }
        }


        return true;
    }

    public boolean pickupDroppedItems() {
        if (this.inventory.isFull()) {
            return false;
        }

        boolean update = false;

        for (Entity entity : this.level.getCollidingEntities(this.pickupArea)) {
            if (!entity.isClosed() && !(entity instanceof EntityItem)) {
                continue;
            }

            EntityItem itemEntity = (EntityItem) entity;
            Item item = itemEntity.getItem();

            if (item.getId() == 0 || item.getCount() < 1) {
                continue;
            }

            int originalCount = item.getCount();
            Item[] items = this.inventory.addItem(item);

            if (items.length == 0) {
                entity.close();
                update = true;
                continue;
            }

            if (items[0].getCount() != originalCount) {
                update = true;
                item.setCount(items[0].getCount());
            }
        }

        BlockEntity blockEntity = this.level.getBlockEntity(this.up());
        //Fix for furnace outputs
        if (blockEntity instanceof BlockEntityFurnace) {
            FurnaceInventory inv = ((BlockEntityFurnace) blockEntity).getInventory();
            Item item = inv.getResult();
            if (item.getId() != 0 && item.getCount() > 0) {
                Item itemToAdd = item.clone();
                itemToAdd.count = 1;
                Item[] items = this.inventory.addItem(itemToAdd);
                if (items.length < 1) {
                    item.count--;
                    if (item.count <= 0) {
                        item = Item.get(0);
                    }
                    inv.setResult(item);
                    update = true;
                }
            }
        } else if (blockEntity instanceof InventoryHolder) {
            Inventory inv = ((InventoryHolder) blockEntity).getInventory();

            for (int i = 0; i < inv.getSize(); i++) {
                Item item = inv.getItem(i);

                if (item.getId() != 0 && item.getCount() > 0) {
                    Item itemToAdd = item.clone();
                    itemToAdd.count = 1;

                    Item[] items = this.inventory.addItem(itemToAdd);

                    if (items.length >= 1) {
                        continue;
                    }

                    item.count--;

                    if (item.count <= 0) {
                        item = Item.get(0);
                    }

                    inv.setItem(i, item);
                    update = true;
                    break;
                }
            }
        }

        //TODO: check for minecart
        return update;
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

        for (Item content : inventory.getContents().values()) {
            level.dropItem(this, content);
        }
    }

    public boolean transferItemsOut() {
        if (this.inventory.isEmpty()) {
            return false;
        }

        BlockEntity be = this.level.getBlockEntity(this.getSide(BlockFace.fromIndex(this.level.getBlockDataAt(this.getFloorX(), this.getFloorY(), this.getFloorZ()))));
        
        if (be instanceof BlockEntityHopper && this.getBlock().getDamage() == 0) return false;
        //Fix for furnace inputs
        if (be instanceof BlockEntityFurnace) {
            BlockEntityFurnace furnace = (BlockEntityFurnace) be;
            FurnaceInventory inventory = furnace.getInventory();
            if (inventory.isFull()) {
                return false;
            }

            for (int i = 0; i < this.inventory.getSize(); i++) {
                Item item = this.inventory.getItem(i);
                if (item.getId() != 0 && item.getCount() > 0) {
                    Item itemToAdd = item.clone();
                    itemToAdd.setCount(1);
                    
                    //Check direction of hopper
                    if (this.getBlock().getDamage() == 0) {
                        if (inventory.getSmelting().getId() == Item.AIR) {
                            inventory.setSmelting(itemToAdd);
                            item.count--;
                        } else if (inventory.getSmelting().getId() == itemToAdd.getId()) {
                            Item smelting = inventory.getSmelting();
                            if (smelting.count < smelting.getMaxStackSize()) {
                                smelting.count++;
                                inventory.setSmelting(smelting);
                                item.count--;
                            }
                        }
                    } else if (Fuel.duration.containsKey(item.getId())) {
                        if (inventory.getFuel().getId() == Item.AIR){
                            inventory.setFuel(itemToAdd);
                            item.count--;
                        } else if (inventory.getFuel().getId() == itemToAdd.getId()) {
                            Item fuel = inventory.getFuel();
                            if (fuel.count < fuel.getMaxStackSize()) {
                                fuel.count++;
                                inventory.setFuel(fuel);
                                item.count--;
                            }
                        }
                    }
                    inventory.sendContents(inventory.getViewers());
                    this.inventory.setItem(i, item);
                }
            }

        } else if (be instanceof InventoryHolder) {
            Inventory inventory = ((InventoryHolder) be).getInventory();

            if (inventory.isFull()) {
                return false;
            }

            for (int i = 0; i < this.inventory.getSize(); i++) {
                Item item = this.inventory.getItem(i);

                if (item.getId() != 0 && item.getCount() > 0) {
                    Item itemToAdd = item.clone();
                    itemToAdd.setCount(1);

                    Item[] items = inventory.addItem(itemToAdd);

                    if (items.length > 0) {
                        continue;
                    }

                    inventory.sendContents(inventory.getViewers()); //whats wrong?
                    item.count--;
                    this.inventory.setItem(i, item);
                    return true;
                }
            }
        }
        //TODO: check for minecart
        return false;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag c = new CompoundTag()
                .putString("id", BlockEntity.HOPPER)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        if (this.hasName()) {
            c.put("CustomName", this.namedTag.get("CustomName"));
        }

        return c;
    }
}

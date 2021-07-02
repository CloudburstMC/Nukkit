package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.MinecartChestInventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.MinecartType;

/**
 * @author Snake1999
 * @since 2016/1/30
 */
public class EntityMinecartChest extends EntityMinecartAbstract implements InventoryHolder {

    public static final int NETWORK_ID = 98;

    protected MinecartChestInventory inventory;

    public EntityMinecartChest(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        setDisplayBlock(Block.get(Block.CHEST), false);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    @Override
    public String getOriginalName() {
        return getType().getName();
    }

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(1);
    }

    @Override
    public boolean isRideable(){
        return false;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void dropItem() {
        super.dropItem();

        this.level.dropItem(this, Item.get(Item.CHEST));
        for (Item item : this.inventory.getContents().values()) {
            this.level.dropItem(this, item);
        }
        this.inventory.clearAll();
    }

    @Override
    public boolean mountEntity(Entity entity, byte mode) {
        return false;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        player.addWindow(this.inventory);
        return false; // If true, the count of items player has in hand decreases
    }

    @Override
    public MinecartChestInventory getInventory() {
        return inventory;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.inventory = new MinecartChestInventory(this);
        if (this.namedTag.contains("Items") && this.namedTag.get("Items") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = this.namedTag.getList("Items", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                this.inventory.setItem(item.getByte("Slot"), NBTIO.getItemHelper(item));
            }
        }

        this.dataProperties
                .putByte(DATA_CONTAINER_TYPE, 10)
                .putInt(DATA_CONTAINER_BASE_SIZE, this.inventory.getSize())
                .putInt(DATA_CONTAINER_EXTRA_SLOTS_PER_STRENGTH, 0);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        if (this.inventory != null) {
            for (int slot = 0; slot < 27; ++slot) {
                Item item = this.inventory.getItem(slot);
                if (item != null && item.getId() != Item.AIR) {
                    this.namedTag.getList("Items", CompoundTag.class)
                            .add(NBTIO.putItemHelper(item, slot));
                }
            }
        }
    }
}

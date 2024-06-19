package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
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
 * Created by Snake1999 on 2016/1/30.
 * Package cn.nukkit.entity.item in project Nukkit.
 */
public class EntityMinecartChest extends EntityMinecartAbstract implements InventoryHolder {

    public static final int NETWORK_ID = 98;

    protected MinecartChestInventory inventory;

    public EntityMinecartChest(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        setDisplayBlock(Block.get(Block.CHEST), false);
        setName("Minecart with Chest");
    }

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(1);
    }

    @Override
    public boolean isRideable() {
        return false;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void dropItem() {
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) this.lastDamageCause).getDamager();
            if (damager instanceof Player && ((Player) damager).isCreative()) {
                return;
            }
        }
        this.level.dropItem(this, Item.get(Item.CHEST_MINECART));

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

    @Override
    public boolean mountEntity(Entity entity, byte mode) {
        return false;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (this.isAlive()) {
            player.addWindow(this.getInventory());
        }
        return false; // If true, the count of items player has in hand decreases
    }

    @Override
    public MinecartChestInventory getInventory() {
        if (this.inventory == null) {
            this.initInventory();
        }
        return inventory;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.dataProperties
                .putByte(DATA_CONTAINER_TYPE, 10)
                .putInt(DATA_CONTAINER_BASE_SIZE, 27)
                .putInt(DATA_CONTAINER_EXTRA_SLOTS_PER_STRENGTH, 0);
    }

    private void initInventory() {
        if (!this.namedTag.contains("Items") || !(this.namedTag.get("Items") instanceof ListTag)) {
            this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        }
        ListTag<CompoundTag> list = (ListTag<CompoundTag>) this.namedTag.getList("Items");

        this.inventory = new MinecartChestInventory(this);

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
    public String getInteractButtonText() {
        return "action.interact.opencontainer";
    }
}

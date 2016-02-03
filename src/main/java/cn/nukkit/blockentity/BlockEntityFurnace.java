package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockFurnace;
import cn.nukkit.block.BlockFurnaceBurning;
import cn.nukkit.event.inventory.FurnaceBurnEvent;
import cn.nukkit.event.inventory.FurnaceSmeltEvent;
import cn.nukkit.inventory.FurnaceInventory;
import cn.nukkit.inventory.FurnaceRecipe;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.ContainerSetDataPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockEntityFurnace extends BlockEntitySpawnable implements InventoryHolder, BlockEntityContainer, BlockEntityNameable {

    protected FurnaceInventory inventory;

    public BlockEntityFurnace(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.inventory = new FurnaceInventory(this);

        if (!this.namedTag.contains("Items") || !(this.namedTag.get("Items") instanceof ListTag)) {
            this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        }

        for (int i = 0; i < this.getSize(); i++) {
            this.inventory.setItem(i, this.getItem(i));
        }

        if (!this.namedTag.contains("BurnTime") || this.namedTag.getShort("BurnTime") < 0) {
            this.namedTag.putShort("BurnTime", 0);
        }

        if (!this.namedTag.contains("CookTime") || this.namedTag.getShort("CookTime") < 0 || (this.namedTag.getShort("BurnTime") == 0 && this.namedTag.getShort("CookTime") > 0)) {
            this.namedTag.putShort("CookTime", 0);
        }

        if (!this.namedTag.contains("MaxTime")) {
            this.namedTag.putShort("MaxTime", this.namedTag.getShort("BurnTime"));
            this.namedTag.putShort("BurnDuration", 0);
        }

        if (this.namedTag.contains("BurnTicks")) {
            this.namedTag.putShort("BurnDuration", this.namedTag.getShort("BurnTicks"));
            this.namedTag.remove("BurnTicks");
        }

        if (this.namedTag.getShort("BurnTime") > 0) {
            this.scheduleUpdate();
        }
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Furnace";
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

    @Override
    public void close() {
        if (!this.closed) {
            for (Player player : this.getInventory().getViewers()) {
                player.removeWindow(this.getInventory());
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
    public int getSize() {
        return 3;
    }

    protected int getSlotIndex(int index) {
        ListTag<CompoundTag> list = this.namedTag.getList("Items", CompoundTag.class);
        for (int i = 0; i < list.size(); i++) {
            if ((list.get(i).getByte("Slot") & 0xff) == index) {
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
            return Item.get(data.getShort("id"), data.getShort("Damage"), data.getByte("Count") & 0xff);
        }
    }

    @Override
    public void setItem(int index, Item item) {
        int i = this.getSlotIndex(index);

        CompoundTag d = new CompoundTag()
                .putByte("Count", (byte) item.getCount())
                .putByte("Slot", (byte) index)
                .putShort("id", item.getId())
                .putShort("Damage", item.getDamage());

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
    public FurnaceInventory getInventory() {
        return inventory;
    }

    protected void checkFuel(Item fuel) {

        FurnaceBurnEvent ev = new FurnaceBurnEvent(this, fuel, fuel.getFuelTime() == null ? 0 : fuel.getFuelTime());

        if (ev.isCancelled()) {
            return;
        }

        this.namedTag.putShort("MaxTime", ev.getBurnTime());
        this.namedTag.putShort("BurnTime", ev.getBurnTime());
        this.namedTag.putShort("BurnDuration", 0);
        if (this.getBlock().getId() == Item.FURNACE) {
            this.getLevel().setBlock(this, new BlockFurnaceBurning(this.getBlock().getDamage()), true);
        }

        if (this.namedTag.getShort("BurnTime") > 0 && ev.isBurning()) {
            fuel.setCount(fuel.getCount() - 1);
            if (fuel.getCount() == 0) {
                fuel = new ItemBlock(new BlockAir(), 0, 0);
            }
            this.inventory.setFuel(fuel);
        }
    }

    @Override
    public boolean onUpdate() {
        if (this.closed) {
            return false;
        }

        boolean ret = false;
        Item fuel = this.inventory.getFuel();
        Item raw = this.inventory.getSmelting();
        Item product = this.inventory.getResult();
        FurnaceRecipe smelt = this.server.getCraftingManager().matchFurnaceRecipe(raw);
        boolean canSmelt = (smelt != null && raw.getCount() > 0 && ((smelt.getResult().equals(product, true) && product.getCount() < product.getMaxStackSize()) || product.getId() == Item.AIR));

        if (this.namedTag.getShort("BurnTime") <= 0 && canSmelt && fuel.getFuelTime() != null && fuel.getCount() > 0) {
            this.checkFuel(fuel);
        }

        if (this.namedTag.getShort("BurnTime") > 0) {
            this.namedTag.putShort("BurnTime", (this.namedTag.getShort("BurnTime") - 1));
            this.namedTag.putShort("BurnDuration", (int) Math.ceil((double) this.namedTag.getShort("BurnTime") / (double) this.namedTag.getShort("MaxTime") * 200d));

            if (smelt != null && canSmelt) {
                this.namedTag.putShort("CookTime", (this.namedTag.getShort("CookTime") + 1));
                if (this.namedTag.getShort("CookTime") >= 200) {
                    product = Item.get(smelt.getResult().getId(), smelt.getResult().getDamage(), product.getCount() + 1);

                    FurnaceSmeltEvent ev = new FurnaceSmeltEvent(this, raw, product);
                    this.server.getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.inventory.setResult(ev.getResult());
                        raw.setCount(raw.getCount() - 1);
                        if (raw.getCount() == 0) {
                            raw = new ItemBlock(new BlockAir(), 0, 0);
                        }
                        this.inventory.setSmelting(raw);
                    }

                    this.namedTag.putShort("CookTime", (this.namedTag.getShort("CookTime") - 200));
                }
            } else if (this.namedTag.getShort("BurnTime") <= 0) {
                this.namedTag.putShort("BurnTime", 0);
                this.namedTag.putShort("CookTime", 0);
                this.namedTag.putShort("BurnDuration", 0);
            } else {
                this.namedTag.putShort("CookTime", 0);
            }
            ret = true;
        } else {
            if (this.getBlock().getId() == Item.BURNING_FURNACE) {
                this.getLevel().setBlock(this, new BlockFurnace(this.getBlock().getDamage()), true);
            }
            this.namedTag.putShort("BurnTime", 0);
            this.namedTag.putShort("CookTime", 0);
            this.namedTag.putShort("BurnDuration", 0);
        }

        for (Player player : this.getInventory().getViewers()) {
            int windowId = player.getWindowId(this.getInventory());
            if (windowId > 0) {
                ContainerSetDataPacket pk = new ContainerSetDataPacket();
                pk.windowid = (byte) windowId;
                pk.property = 0;
                pk.value = this.namedTag.getShort("CookTime");
                player.dataPacket(pk);

                pk = new ContainerSetDataPacket();
                pk.windowid = (byte) windowId;
                pk.property = 1;
                pk.value = this.namedTag.getShort("BurnDuration");
                player.dataPacket(pk);
            }
        }

        this.lastUpdate = System.currentTimeMillis();

        return ret;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag c = new CompoundTag()
                .putString("id", BlockEntity.FURNACE)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putShort("BurnDuration", this.namedTag.getShort("BurnDuration"))
                .putShort("BurnTime", this.namedTag.getShort("BurnTime"))
                .putShort("CookTime", this.namedTag.getShort("CookTime"));

        if (this.hasName()) {
            c.put("CustomName", this.namedTag.get("CustomName"));
        }

        return c;
    }
}

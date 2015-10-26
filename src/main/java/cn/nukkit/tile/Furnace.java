package cn.nukkit.tile;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.inventory.FurnaceBurnEvent;
import cn.nukkit.event.inventory.FurnaceSmeltEvent;
import cn.nukkit.inventory.FurnaceInventory;
import cn.nukkit.inventory.FurnaceRecipe;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.Network;
import cn.nukkit.network.protocol.ContainerSetDataPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Furnace extends Tile implements InventoryHolder, Container {

    protected FurnaceInventory inventory;

    public Furnace(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.inventory = new FurnaceInventory(this);

        if (!this.namedTag.contains("Items") || !(this.namedTag.get("Items") instanceof ListTag)) {
            this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        }

        for (int i = 0; i < this.getSize(); i++) {
            this.inventory.setItem(i, this.getItem(i));
        }

        if (!this.namedTag.contains("BurnTime") || this.namedTag.getShort("BurnTime") < 0) {
            this.namedTag.putShort("BurnTime", (short) 0);
        }

        if (!this.namedTag.contains("BurnTime") || this.namedTag.getShort("CookTime") < 0 || (this.namedTag.getShort("BurnTime") == 0 && this.namedTag.getShort("CookTime") > 0)) {
            this.namedTag.putShort("CookTime", (short) 0);
        }

        if (!this.namedTag.contains("MaxTime")) {
            this.namedTag.putShort("MaxTime", this.namedTag.getShort("BurnTime"));
            this.namedTag.putShort("BurnTicks", (short) 0);
        }

        if (this.namedTag.getShort("BurnTime") > 0) {
            this.scheduleUpdate();
        }
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
        ListTag<CompoundTag> list = (ListTag<CompoundTag>) this.namedTag.getList("Items");
        for (int i = 0; i < list.size(); i++) {
            if (list.list.get(i).getByte("Slot") == index) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public Item getItem(int index) {
        int i = this.getSlotIndex(index);
        if (i < 0) {
            return Item.get(Item.AIR, 0, 0);
        } else {
            CompoundTag data = (CompoundTag) this.namedTag.getList("Items").get(i);
            return Item.get(data.getShort("id"), (int) data.getShort("Damage"), data.getByte("Count"));
        }
    }

    @Override
    public void setItem(int index, Item item) {
        int i = this.getSlotIndex(index);

        CompoundTag d = new CompoundTag()
                .putByte("Count", (byte) item.getCount())
                .putByte("Slot", (byte) index)
                .putShort("id", (short) item.getId())
                .putShort("Damage", item.getDamage());

        if (item.getId() == Item.AIR || item.getCount() <= 0) {
            if (i >= 0) {
                this.namedTag.getList("Items").list.remove(i);
            }
        } else if (i < 0) {
            i = this.namedTag.getList("Items").list.size();
            i = Math.max(i, this.getSize());
            ((ListTag<CompoundTag>) this.namedTag.getList("Items")).list.add(i, d);
        } else {
            ((ListTag<CompoundTag>) this.namedTag.getList("Items")).list.add(i, d);
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
        this.namedTag.putShort("BurnTicks", (short) 0);
        if (this.getBlock().getId() == Item.FURNACE) {
            this.getLevel().setBlock(this, Block.get(Item.BURNING_FURNACE, (int) this.getBlock().getDamage()), true);
        }

        if (this.namedTag.getShort("BurnTime") > 0 && ev.isBurning()) {
            fuel.setCount(fuel.getCount() - 1);
            if (fuel.getCount() == 0) {
                fuel = Item.get(Item.AIR, 0, 0);
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
            this.namedTag.putShort("BurnTime", (short) (this.namedTag.getShort("BurnTime") + 1));
            this.namedTag.putShort("BurnTicks", (short) Math.ceil((double) this.namedTag.getShort("BurnTime") / (double) this.namedTag.getShort("MaxTime") * 200d));

            if (smelt != null && canSmelt) {
                this.namedTag.putShort("CookTime", (short) (this.namedTag.getShort("CookTime") + 1));
                if (this.namedTag.getShort("CookTime") >= 200) {
                    product = Item.get(smelt.getResult().getId(), Integer.valueOf(smelt.getResult().getDamage()), product.getCount() + 1);

                    FurnaceSmeltEvent ev = new FurnaceSmeltEvent(this, raw, product);
                    this.server.getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.inventory.setResult(ev.getResult());
                        raw.setCount(raw.getCount() - 1);
                        if (raw.getCount() == 0) {
                            raw = Item.get(Item.AIR, 0, 0);
                        }
                        this.inventory.setSmelting(raw);
                    }

                    this.namedTag.putShort("CookTime", (short) (this.namedTag.getShort("CookTime") - 200));
                }
            } else if (this.namedTag.getShort("BurnTime") <= 0) {
                this.namedTag.putShort("BurnTime", (short) 0);
                this.namedTag.putShort("CookTime", (short) 0);
                this.namedTag.putShort("BurnTicks", (short) 0);
            } else {
                this.namedTag.putShort("CookTime", (short) 0);
            }
            ret = true;
        } else {
            if (this.getBlock().getId() == Item.BURNING_FURNACE) {
                this.getLevel().setBlock(this, Block.get(Item.FURNACE, (int) this.getBlock().getDamage()), true);
            }
            this.namedTag.putShort("BurnTime", (short) 0);
            this.namedTag.putShort("CookTime", (short) 0);
            this.namedTag.putShort("BurnTicks", (short) 0);
        }

        for (Player player : this.getInventory().getViewers()) {
            int windowId = player.getWindowId(this.getInventory());
            if (windowId > 0) {
                ContainerSetDataPacket pk = new ContainerSetDataPacket();
                pk.windowid = (byte) windowId;
                pk.property = 0;
                pk.value = this.namedTag.getShort("CookTime");
                player.dataPacket(pk.setChannel(Network.CHANNEL_WORLD_EVENTS));

                pk = new ContainerSetDataPacket();
                pk.windowid = (byte) windowId;
                pk.property = 1;
                pk.value = this.namedTag.getShort("BurnTicks");
                player.dataPacket(pk.setChannel(Network.CHANNEL_WORLD_EVENTS));
            }
        }

        this.lastUpdate = System.currentTimeMillis();

        return ret;
    }
}

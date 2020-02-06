package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.event.inventory.FurnaceBurnEvent;
import cn.nukkit.event.inventory.FurnaceSmeltEvent;
import cn.nukkit.inventory.FurnaceInventory;
import cn.nukkit.inventory.FurnaceRecipe;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.ContainerSetDataPacket;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;

import java.util.HashSet;

import static cn.nukkit.block.BlockIds.*;
import static cn.nukkit.item.ItemIds.BUCKET;

/**
 * @author MagicDroidX
 */
public class BlockEntityFurnace extends BlockEntitySpawnable implements InventoryHolder, BlockEntityContainer, BlockEntityNameable {

    protected FurnaceInventory inventory;

    protected int burnTime = 0;
    protected int cookTime = 0;
    protected int maxTime = 0;

    public BlockEntityFurnace(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        this.inventory = new FurnaceInventory(this, this.getInventoryType());

        if (!this.namedTag.contains("Items") || !(this.namedTag.get("Items") instanceof ListTag)) {
            this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        }

        for (int i = 0; i < this.getSize(); i++) {
            this.inventory.setItem(i, this.getItem(i));
        }

        if (!this.namedTag.contains("BurnTime") || this.namedTag.getShort("BurnTime") < 0) {
            burnTime = 0;
        } else {
            burnTime = this.namedTag.getShort("BurnTime");
        }

        if (!this.namedTag.contains("CookTime") || this.namedTag.getShort("CookTime") < 0 || (this.namedTag.getShort("BurnTime") == 0 && this.namedTag.getShort("CookTime") > 0)) {
            cookTime = 0;
        } else {
            cookTime = this.namedTag.getShort("CookTime");
        }

        if (!this.namedTag.contains("MaxTime")) {
            maxTime = burnTime;
        } else {
            maxTime = this.namedTag.getShort("MaxTime");
        }

        if (burnTime > 0) {
            this.scheduleUpdate();
        }

        super.initBlockEntity();
    }

    public InventoryType getInventoryType() {
        return InventoryType.FURNACE;
    }

    public float getBurnRate() {
        return 1.0f;
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

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putList(new ListTag<CompoundTag>("Items"));
        for (int index = 0; index < this.getSize(); index++) {
            this.setItem(index, this.inventory.getItem(index));
        }

        this.namedTag.putShort("CookTime", cookTime);
        this.namedTag.putShort("BurnTime", burnTime);
        this.namedTag.putShort("MaxTime", maxTime);
    }

    @Override
    public boolean isBlockEntityValid() {
        Identifier blockId = getBlock().getId();
        return blockId == BlockIds.FURNACE || blockId == BlockIds.LIT_FURNACE;
    }

    @Override
    public int getSize() {
        return 3;
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

        if (item.getId() == AIR || item.getCount() <= 0) {
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
        this.server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }

        maxTime = (int) (ev.getBurnTime() / getBurnRate());
        burnTime = (int) (ev.getBurnTime() / getBurnRate());

        if (this.getBlock().getId() == BlockIds.FURNACE
                || this.getBlock().getId() == BlockIds.SMOKER
                || this.getBlock().getId() == BlockIds.BLAST_FURNACE) {
            lightFurnace();
        }

        if (burnTime > 0 && ev.isBurning()) {
            for (Player p : this.getInventory().getViewers()) {
                ContainerSetDataPacket pk = new ContainerSetDataPacket();
                pk.windowId = p.getWindowId(this.getInventory());
                pk.property = ContainerSetDataPacket.PROPERTY_FURNACE_LIT_DURATION;
                pk.value = maxTime;

                p.dataPacket(pk);
            }
            fuel.setCount(fuel.getCount() - 1);
            if (fuel.getCount() == 0) {
                if (fuel.getId() == BUCKET && fuel.getDamage() == 10) {
                    fuel.setDamage(0);
                    fuel.setCount(1);
                } else {
                    fuel = Item.get(AIR, 0, 0);
                }
            }
            this.inventory.setFuel(fuel);
        }
    }

    @Override
    public boolean onUpdate() {
        if (this.closed) {
            return false;
        }

        this.timing.startTiming();

        boolean ret = false;
        Item fuel = this.inventory.getFuel();
        Item raw = this.inventory.getSmelting();
        Item product = this.inventory.getResult();

        Identifier blockId = getBlock().getId();
        FurnaceRecipe smelt = this.server.getCraftingManager().matchFurnaceRecipe(raw, blockId);
        boolean canSmelt = (smelt != null && raw.getCount() > 0 && ((smelt.getResult().equals(product, true)
                && product.getCount() < product.getMaxStackSize()) || product.getId() == AIR));

        if (burnTime <= 0 && canSmelt && fuel.getFuelTime() != null && fuel.getCount() > 0) {
            this.checkFuel(fuel);
        }
        if (burnTime > 0) {
            boolean fastCook = (blockId == LIT_SMOKER || blockId == LIT_BLAST_FURNACE);
            burnTime--;

            if (smelt != null && canSmelt) {
                cookTime++;
                if ((fastCook && cookTime >= 100) || cookTime >= 200) {
                    product = Item.get(smelt.getResult().getId(), smelt.getResult().getDamage(), product.getCount() + 1);

                    FurnaceSmeltEvent ev = new FurnaceSmeltEvent(this, raw, product);
                    this.server.getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.inventory.setResult(ev.getResult());
                        raw.setCount(raw.getCount() - 1);
                        if (raw.getCount() == 0) {
                            raw = Item.get(AIR, 0, 0);
                        }
                        this.inventory.setSmelting(raw);
                    }

                    cookTime -= fastCook ? 100 : 200;
                }
            } else if (burnTime <= 0) {
                burnTime = 0;
                cookTime = 0;
            } else {
                cookTime = 0;
            }
            ret = true;
        } else {
            if (blockId == BlockIds.LIT_FURNACE || blockId == LIT_BLAST_FURNACE || blockId == LIT_SMOKER) {
                extinguishFurnace();
            }
            burnTime = 0;
            cookTime = 0;
        }

        for (Player player : this.getInventory().getViewers()) {
            int windowId = player.getWindowId(this.getInventory());
            if (windowId > 0) {
                ContainerSetDataPacket pk = new ContainerSetDataPacket();
                pk.windowId = windowId;
                pk.property = ContainerSetDataPacket.PROPERTY_FURNACE_TICK_COUNT;

                pk.value = cookTime;
                player.dataPacket(pk);

                pk = new ContainerSetDataPacket();
                pk.windowId = windowId;
                pk.property = ContainerSetDataPacket.PROPERTY_FURNACE_LIT_TIME;
                pk.value = burnTime;
                player.dataPacket(pk);
            }
        }

        this.lastUpdate = System.currentTimeMillis();

        this.timing.stopTiming();

        return ret;
    }

    protected void extinguishFurnace() {
        this.getLevel().setBlock(this, Block.get(BlockIds.FURNACE, this.getBlock().getDamage()), true);
    }

    protected void lightFurnace() {
        this.getLevel().setBlock(this, Block.get(BlockIds.LIT_FURNACE, this.getBlock().getDamage()), true);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag c = new CompoundTag()
                .putString("id", getSaveId())
                .putInt("x", this.x)
                .putInt("y", this.y)
                .putInt("z", this.z)
                .putShort("BurnTime", burnTime)
                .putShort("CookTime", cookTime);

        if (this.hasName()) {
            c.put("CustomName", this.namedTag.get("CustomName"));
        }

        return c;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }
}

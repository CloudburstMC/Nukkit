package cn.nukkit.blockentity.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.Furnace;
import cn.nukkit.event.inventory.FurnaceBurnEvent;
import cn.nukkit.event.inventory.FurnaceSmeltEvent;
import cn.nukkit.inventory.FurnaceInventory;
import cn.nukkit.inventory.FurnaceRecipe;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemUtils;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.protocol.bedrock.packet.ContainerSetDataPacket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static cn.nukkit.block.BlockIds.*;
import static cn.nukkit.item.ItemIds.BUCKET;

/**
 * @author MagicDroidX
 */
public class FurnaceBlockEntity extends BaseBlockEntity implements Furnace {

    protected final FurnaceInventory inventory;
    protected short burnTime = 0;
    protected short burnDuration = 0;
    protected short cookTime = 0;
    protected short maxTime = 0;

    protected FurnaceBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position, InventoryType inventoryType) {
        super(type, chunk, position);
        this.inventory = new FurnaceInventory(this, inventoryType);
    }

    public FurnaceBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
        InventoryType invType;
        Block furnace = getBlock();
        if (furnace.getId() == BLAST_FURNACE || furnace.getId() == LIT_BLAST_FURNACE) {
            invType = InventoryType.BLAST_FURNACE;
        } else if (furnace.getId() == SMOKER || furnace.getId() == LIT_SMOKER) {
            invType = InventoryType.SMOKER;
        } else {
            invType = InventoryType.FURNACE;
        }
        this.inventory = new FurnaceInventory(this, invType);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForList("Items", CompoundTag.class, tags -> {
            for (CompoundTag itemTag : tags) {
                this.inventory.setItem(itemTag.getByte("Slot"), ItemUtils.deserializeItem(itemTag));
            }
        });
        tag.listenForShort("CookTime", this::setCookTime);
        tag.listenForShort("BurnTime", this::setBurnTime);
        tag.listenForShort("MaxTime", this::setMaxTime);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.shortTag("CookTime", cookTime);
        tag.shortTag("BurnTime", burnTime);
        tag.shortTag("MaxTime", maxTime);
        List<CompoundTag> items = new ArrayList<>();
        for (Map.Entry<Integer, Item> entry : this.inventory.getContents().entrySet()) {
            items.add(ItemUtils.serializeItem(entry.getValue(), entry.getKey()));
        }
        tag.listTag("Items", CompoundTag.class, items);
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

    public float getBurnRate() {
        return 1.0f;
    }

    @Override
    public void onBreak() {
        for (Item content : inventory.getContents().values()) {
            this.getLevel().dropItem(this.getPosition(), content);
        }
    }

    @Override
    public boolean isValid() {
        Identifier blockId = getBlock().getId();
        return blockId == BlockIds.FURNACE || blockId == BlockIds.LIT_FURNACE;
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

        maxTime = (short) (ev.getBurnTime() / getBurnRate());
        burnTime = (short) (ev.getBurnTime() / getBurnRate());

        if (this.getBlock().getId() == BlockIds.FURNACE
                || this.getBlock().getId() == BlockIds.SMOKER
                || this.getBlock().getId() == BlockIds.BLAST_FURNACE) {
            lightFurnace();
        }

        if (burnTime > 0 && ev.isBurning()) {
            for (Player p : this.getInventory().getViewers()) {
                ContainerSetDataPacket packet = new ContainerSetDataPacket();
                packet.setWindowId(p.getWindowId(this.getInventory()));
                packet.setProperty(ContainerSetDataPacket.FURNACE_LIT_DURATION);
                packet.setValue(maxTime);

                p.sendPacket(packet);
            }
            fuel.setCount(fuel.getCount() - 1);
            if (fuel.getCount() == 0) {
                if (fuel.getId() == BUCKET && fuel.getMeta() == 10) {
                    fuel.setMeta(0);
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
            burnTime--;

            if (smelt != null && canSmelt) {
                cookTime++;
                if (cookTime >= (200 / getBurnRate())) {
                    product = Item.get(smelt.getResult().getId(), smelt.getResult().getMeta(), product.getCount() + 1);

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

                    cookTime -= (200 / getBurnRate());
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
            byte windowId = player.getWindowId(this.getInventory());
            if (windowId > 0) {
                ContainerSetDataPacket packet = new ContainerSetDataPacket();
                packet.setWindowId(windowId);
                packet.setProperty(ContainerSetDataPacket.FURNACE_TICK_COUNT);
                packet.setValue(cookTime);
                player.sendPacket(packet);

                packet = new ContainerSetDataPacket();
                packet.setWindowId(windowId);
                packet.setProperty(ContainerSetDataPacket.FURNACE_LIT_TIME);
                packet.setValue(burnTime);
                player.sendPacket(packet);
            }
        }

        this.lastUpdate = System.currentTimeMillis();

        this.timing.stopTiming();

        return ret;
    }

    protected void extinguishFurnace() {
        this.getLevel().setBlock(this.getPosition(), Block.get(BlockIds.FURNACE, this.getBlock().getMeta()), true);
    }

    protected void lightFurnace() {
        this.getLevel().setBlock(this.getPosition(), Block.get(BlockIds.LIT_FURNACE, this.getBlock().getMeta()), true);
    }

    public int getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = (short) burnTime;
        if (burnTime > 0) {
            this.scheduleUpdate();
        }
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = (short) cookTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = (short) maxTime;
    }

    @Override
    public boolean isSpawnable() {
        return true;
    }
}

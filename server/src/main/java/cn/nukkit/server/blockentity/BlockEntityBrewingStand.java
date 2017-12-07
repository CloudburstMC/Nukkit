package cn.nukkit.server.blockentity;

import cn.nukkit.api.event.inventory.BrewEvent;
import cn.nukkit.api.event.inventory.StartBrewEvent;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.Player;
import cn.nukkit.server.block.Block;
import cn.nukkit.server.block.BlockAir;
import cn.nukkit.server.block.BlockBrewingStand;
import cn.nukkit.server.inventory.BrewingInventory;
import cn.nukkit.server.inventory.BrewingRecipe;
import cn.nukkit.server.inventory.InventoryHolder;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemBlock;
import cn.nukkit.server.level.format.FullChunk;
import cn.nukkit.server.nbt.NBTIO;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.nbt.tag.ListTag;
import cn.nukkit.server.network.protocol.ContainerSetDataPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class BlockEntityBrewingStand extends BlockEntitySpawnable implements InventoryHolder, BlockEntityContainer, BlockEntityNameable {

    protected final BrewingInventory inventory;

    public static final int MAX_BREW_TIME = 400;

    public int brewTime = MAX_BREW_TIME;
    public int fuelTotal;
    public int fuelAmount;

    public static final List<Integer> ingredients = new ArrayList<Integer>() {
        {
            addAll(Arrays.asList(Item.NETHER_WART, Item.GOLD_NUGGET, Item.GHAST_TEAR, Item.GLOWSTONE_DUST, Item.REDSTONE_DUST, Item.GUNPOWDER, Item.MAGMA_CREAM, Item.BLAZE_POWDER, Item.GOLDEN_CARROT, Item.SPIDER_EYE, Item.FERMENTED_SPIDER_EYE, Item.GLISTERING_MELON, Item.SUGAR, Item.RAW_FISH));
        }
    };

    public BlockEntityBrewingStand(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        inventory = new BrewingInventory(this);

        if (!namedTag.contains("ItemTypes") || !(namedTag.get("ItemTypes") instanceof ListTag)) {
            namedTag.putList(new ListTag<CompoundTag>("ItemTypes"));
        }

        for (int i = 0; i < getSize(); i++) {
            inventory.setItem(i, this.getItem(i));
        }

        if (!namedTag.contains("CookTime") || namedTag.getShort("CookTime") > MAX_BREW_TIME) {
            this.brewTime = MAX_BREW_TIME;
        } else {
            this.brewTime = namedTag.getShort("CookTime");
        }

        this.fuelAmount = namedTag.getShort("FuelAmount");
        this.fuelTotal = namedTag.getShort("FuelTotal");

        if (brewTime < MAX_BREW_TIME) {
            this.scheduleUpdate();
        }
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Brewing Stand";
    }

    @Override
    public boolean hasName() {
        return namedTag.contains("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.equals("")) {
            namedTag.remove("CustomName");
            return;
        }

        namedTag.putString("CustomName", name);
    }

    @Override
    public void close() {
        if (!closed) {
            for (Player player : new HashSet<>(getInventory().getViewers())) {
                player.removeWindow(getInventory());
            }
            super.close();
        }
    }

    @Override
    public void saveNBT() {
        namedTag.putList(new ListTag<CompoundTag>("ItemTypes"));
        for (int index = 0; index < getSize(); index++) {
            this.setItem(index, inventory.getItem(index));
        }

        namedTag.putShort("CookTime", brewTime);
        namedTag.putShort("FuelAmount", this.fuelAmount);
        namedTag.putShort("FuelTotal", this.fuelTotal);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == Block.BREWING_STAND_BLOCK;
    }

    @Override
    public int getSize() {
        return 4;
    }

    protected int getSlotIndex(int index) {
        ListTag<CompoundTag> list = this.namedTag.getList("ItemTypes", CompoundTag.class);
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
            CompoundTag data = (CompoundTag) this.namedTag.getList("ItemTypes").get(i);
            return NBTIO.getItemHelper(data);
        }
    }

    @Override
    public void setItem(int index, Item item) {
        int i = this.getSlotIndex(index);

        CompoundTag d = NBTIO.putItemHelper(item, index);

        if (item.getId() == Item.AIR || item.getCount() <= 0) {
            if (i >= 0) {
                this.namedTag.getList("ItemTypes").getAll().remove(i);
            }
        } else if (i < 0) {
            (this.namedTag.getList("ItemTypes", CompoundTag.class)).add(d);
        } else {
            (this.namedTag.getList("ItemTypes", CompoundTag.class)).add(i, d);
        }
    }

    @Override
    public BrewingInventory getInventory() {
        return inventory;
    }

    protected boolean checkIngredient(Item ingredient) {
        return ingredients.contains(ingredient.getId());
    }

    @Override
    public boolean onUpdate() {
        if (closed) {
            return false;
        }

        boolean ret = false;

        Item ingredient = this.inventory.getIngredient();
        boolean canBrew = false;

        Item fuel = this.getInventory().getFuel();
        if (this.fuelAmount <= 0 && fuel.getId() == Item.BLAZE_POWDER && fuel.getCount() > 0) {
            fuel.count--;
            this.fuelAmount = 20;
            this.fuelTotal = 20;

            this.inventory.setFuel(fuel);
            this.sendFuel();
        }

        if (this.fuelAmount > 0) {
            for (int i = 1; i <= 3; i++) {
                if (this.inventory.getItem(i).getId() == Item.POTION) {
                    canBrew = true;
                }
            }

            if (this.brewTime <= MAX_BREW_TIME && canBrew && ingredient.getCount() > 0) {
                if (!this.checkIngredient(ingredient)) {
                    canBrew = false;
                }
            } else {
                canBrew = false;
            }
        }

        if (canBrew) {
            if (this.brewTime == MAX_BREW_TIME) {
                this.sendBrewTime();
                StartBrewEvent e = new StartBrewEvent(this);
                this.server.getPluginManager().callEvent(e);

                if (e.isCancelled()) {
                    return false;
                }
            }

            this.brewTime--;

            if (this.brewTime <= 0) { //20 seconds
                BrewEvent e = new BrewEvent(this);
                this.server.getPluginManager().callEvent(e);

                if (!e.isCancelled()) {
                    for (int i = 1; i <= 3; i++) {
                        Item potion = this.inventory.getItem(i);
                        BrewingRecipe recipe = NukkitServer.getInstance().getCraftingManager().matchBrewingRecipe(ingredient, potion);

                        if (recipe != null) {
                            this.inventory.setItem(i, recipe.getResult());
                        }
                    }

                    ingredient.count--;
                    this.inventory.setIngredient(ingredient);

                    this.fuelAmount--;
                    this.sendFuel();
                }

                this.brewTime = MAX_BREW_TIME;
            }

            ret = true;
        } else {
            this.brewTime = MAX_BREW_TIME;
        }

        //this.sendBrewTime();
        lastUpdate = System.currentTimeMillis();

        return ret;
    }

    protected void sendFuel() {
        ContainerSetDataPacket pk = new ContainerSetDataPacket();

        for (Player p : this.inventory.getViewers()) {
            int windowId = p.getWindowId(this.inventory);
            if (windowId > 0) {
                pk.windowId = windowId;

                pk.property = ContainerSetDataPacket.PROPERTY_BREWING_STAND_FUEL_AMOUNT;
                pk.value = this.fuelAmount;
                p.dataPacket(pk);

                pk.property = ContainerSetDataPacket.PROPERTY_BREWING_STAND_FUEL_TOTAL;
                pk.value = this.fuelTotal;
                p.dataPacket(pk);
            }
        }
    }

    protected void sendBrewTime() {
        ContainerSetDataPacket pk = new ContainerSetDataPacket();
        pk.property = ContainerSetDataPacket.PROPERTY_BREWING_STAND_BREW_TIME;
        pk.value = this.brewTime;

        for (Player p : this.inventory.getViewers()) {
            int windowId = p.getWindowId(this.inventory);
            if (windowId > 0) {
                pk.windowId = windowId;

                p.dataPacket(pk);
            }
        }
    }

    public void updateBlock() {
        Block block = this.getLevelBlock();

        if (!(block instanceof BlockBrewingStand)) {
            return;
        }

        int meta = 0;

        for (int i = 1; i <= 3; ++i) {
            Item potion = this.inventory.getItem(i);

            if (potion.getId() == Item.POTION && potion.getCount() > 0) {
                meta |= 1 << i;
            }
        }

        block.setDamage(meta);
        this.level.setBlock(block, block, false, false);
    }

    public int getFuel() {
        return fuelAmount;
    }

    public void setFuel(int fuel) {
        this.fuelAmount = fuel;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = new CompoundTag()
                .putString("id", BlockEntity.BREWING_STAND)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putShort("FuelTotal", this.fuelTotal)
                .putShort("FuelAmount", this.fuelAmount);

        if (this.brewTime < MAX_BREW_TIME) {
            nbt.putShort("CookTime", this.brewTime);
        }

        if (this.hasName()) {
            nbt.put("CustomName", namedTag.get("CustomName"));
        }

        return nbt;
    }
}
package cn.nukkit.blockentity;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.inventory.BrewingInventory;
import cn.nukkit.inventory.BrewingRecipe;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.ContainerSetDataPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class BlockEntityBrewingStand extends BlockEntitySpawnable implements InventoryHolder, BlockEntityContainer, BlockEntityNameable {

    protected final BrewingInventory inventory;

    public static final int MAX_BREW_TIME = 400;

    public int brewTime = MAX_BREW_TIME;

    public static final List<Integer> ingredients = new ArrayList<Integer>() {
        {
            addAll(Arrays.asList(Item.NETHER_WART, Item.GOLD_NUGGET, Item.GHAST_TEAR, Item.GLOWSTONE_DUST, Item.REDSTONE_DUST, Item.GUNPOWDER, Item.MAGMA_CREAM, Item.BLAZE_POWDER, Item.GOLDEN_CARROT, Item.SPIDER_EYE, Item.FERMENTED_SPIDER_EYE, Item.GLISTERING_MELON, Item.SUGAR, Item.RAW_FISH));
        }
    };

    public BlockEntityBrewingStand(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        inventory = new BrewingInventory(this);

        if (!namedTag.contains("Items") || !(namedTag.get("Items") instanceof ListTag)) {
            namedTag.putList(new ListTag<CompoundTag>("Items"));
        }

        for (int i = 0; i < getSize(); i++) {
            inventory.setItem(i, this.getItem(i));
        }

        if (!namedTag.contains("CookTime") || namedTag.getShort("CookTime") > MAX_BREW_TIME) {
            this.brewTime = MAX_BREW_TIME;
        } else {
            this.brewTime = namedTag.getShort("CookTime");
        }

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
        namedTag.putList(new ListTag<CompoundTag>("Items"));
        for (int index = 0; index < getSize(); index++) {
            this.setItem(index, inventory.getItem(index));
        }

        namedTag.putShort("CookTime", brewTime);
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

        if (canBrew) {
            this.brewTime--;

            for (Player player : this.inventory.getViewers()) {
                int windowId = player.getWindowId(this.inventory);
                if (windowId > 0) {
                    ContainerSetDataPacket pk = new ContainerSetDataPacket();
                    pk.windowid = (byte) windowId;
                    pk.property = 0;
                    pk.value = this.brewTime;
                    player.dataPacket(pk);
                }
            }

            if (this.brewTime <= 0) { //20 seconds
                for (int i = 1; i <= 3; i++) {
                    Item potion = this.inventory.getItem(i);
                    BrewingRecipe recipe = Server.getInstance().getCraftingManager().matchBrewingRecipe(ingredient, potion);

                    if (recipe != null) {
                        this.inventory.setItem(i, recipe.getResult());
                    }
                }

                ingredient.count--;
                this.inventory.setIngredient(ingredient);

                this.brewTime = MAX_BREW_TIME;
            }

            ret = true;
        } else {
            this.brewTime = MAX_BREW_TIME;
        }

        lastUpdate = System.currentTimeMillis();

        return ret;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = new CompoundTag()
                .putString("id", BlockEntity.BREWING_STAND)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putShort("CookTime", 0);

        if (this.hasName()) {
            nbt.put("CustomName", namedTag.get("CustomName"));
        }

        return nbt;
    }
}
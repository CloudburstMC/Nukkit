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
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.ContainerSetDataPacket;

import java.util.ArrayList;
import java.util.List;

public class BlockEntityBrewingStand extends BlockEntitySpawnable implements InventoryHolder, BlockEntityContainer, BlockEntityNameable {

    protected BrewingInventory inventory;

    public static final int MAX_BREW_TIME = 400;

    public static List<Integer> ingredients = new ArrayList<>();

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
            namedTag.putShort("CookTime", MAX_BREW_TIME);
        }

        if (namedTag.getShort("CookTime") < MAX_BREW_TIME) {
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
        if (name.equals("")) {
            namedTag.remove("CustomName");
            return;
        }

        namedTag.putString("CustomName", name);
    }

    @Override
    public void close() {
        if (!closed) {
            for (Player player : getInventory().getViewers()) {
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

        Item ingredient = inventory.getIngredient();
        boolean canBrew = false;

        for (int i = 1; i <= 3; i++) {
            if (this.inventory.getItem(i).getId() == Item.POTION) {
                canBrew = true;
            }
        }

        if (namedTag.getShort("CookTime") <= MAX_BREW_TIME && canBrew && ingredient.getCount() > 0) {
            if (!this.checkIngredient(ingredient)) {
                canBrew = false;
            }
        } else {
            canBrew = false;
        }

        if (canBrew) {
            namedTag.putShort("CookTime", namedTag.getShort("CookTime"));

            if (namedTag.getShort("CookTime") <= 0) { //20 seconds
                for (int i = 1; i <= 3; i++) {
                    Item potion = this.inventory.getItem(i);
                    BrewingRecipe recipe = Server.getInstance().getCraftingManager().matchBrewingRecipe(ingredient, potion);

                    if (recipe != null) {
                        this.inventory.setItem(i, recipe.getResult());
                    }
                }

                ingredient.count--;
                this.inventory.setIngredient(ingredient);

                namedTag.putShort("CookTime", namedTag.getShort("CookTime"));
            }

            for (Player player : getInventory().getViewers()) {
                int windowId = player.getWindowId(getInventory());
                if (windowId > 0) {
                    ContainerSetDataPacket pk = new ContainerSetDataPacket();
                    pk.windowid = (byte) windowId;
                    pk.property = 0;
                    pk.value = namedTag.getShort("CookTime");
                    player.dataPacket(pk);
                }

            }

            ret = true;
        } else {
            namedTag.putShort("CookTime", MAX_BREW_TIME);
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
                .putShort("CookTime", MAX_BREW_TIME);

        if (this.hasName()) {
            nbt.put("CustomName", namedTag.get("CustomName"));
        }

        return nbt;
    }
}
package cn.nukkit.blockentity;

import cn.nukkit.block.BlockCampfire;
import cn.nukkit.block.BlockIds;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemEdible;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityCampfire extends BlockEntitySpawnable {
    private Item[] itemsInFire;
    private int[] cookingTimes;

    public BlockEntityCampfire(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        itemsInFire = new Item[4];
        cookingTimes = new int[4];

        boolean hasItems = false;
        for (int i = 1; i <= itemsInFire.length; i++) {
            if (namedTag.contains(("Item" + i))) {
                Item item = NBTIO.getItemHelper(namedTag.getCompound("Item" + i));
                itemsInFire[i - 1] = item;
                hasItems = true;
            } else {
                itemsInFire[i - 1] = null;
            }

            if (namedTag.contains("ItemTime" + i)) {
                cookingTimes[i - 1] = namedTag.getInt("ItemTime" + i);
            } else {
                cookingTimes[i - 1] = 0;
            }
        }
        if (hasItems) this.scheduleUpdate();
        super.initBlockEntity();
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag tag = new CompoundTag()
                .putString("id", BlockEntity.CAMPFIRE)
                .putInt("x", this.x)
                .putInt("y", this.y)
                .putInt("z", this.z);
        for (int i = 1; i <= itemsInFire.length; i++) {
            if (itemsInFire[i - 1] != null) {
                tag.put("Item" + i, NBTIO.putItemHelper(itemsInFire[i - 1]));
            }
            tag.putInt("ItemTime" + i, cookingTimes[i - 1]);
        }
        return tag;
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevel().getBlockAt(this.getX(), this.getY(), this.getZ()).getId() == BlockIds.CAMPFIRE;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        for (int i = 1; i <= itemsInFire.length; i++) {
            if (itemsInFire[i - 1] != null) {
                namedTag.put("Item" + i, NBTIO.putItemHelper(itemsInFire[i - 1]));
                namedTag.putInt("ItemTime" + i, cookingTimes[i - 1]);
            }
        }
    }

    @Override
    public boolean onUpdate() {
        if (this.closed) {
            return false;
        }
        if (!((BlockCampfire) getBlock()).isLit()) {
            return false;
        }

        boolean haveUpdate = false;
        boolean itemChange = false;
        for (int i = 0; i < itemsInFire.length; i++) {
            if (itemsInFire[i] != null) {
                if (++cookingTimes[i] >= 600) {
                    Item output = getLevel().getServer().getCraftingManager().matchFurnaceRecipe(itemsInFire[i]).getResult();
                    getLevel().dropItem(this.add(0.5, 0.5, 0.5), output, null, true, 5);
                    itemsInFire[i] = null;
                    cookingTimes[i] = 0;
                    itemChange = true;
                }
                haveUpdate = true;
            }
        }

        this.lastUpdate = System.currentTimeMillis();
        if (itemChange) spawnToAll();
        return haveUpdate;
    }

    @Override
    public void onBreak() {
        for (int i = 0; i < itemsInFire.length; i++) {
            if (itemsInFire[i] != null) {
                getLevel().dropItem(this, itemsInFire[i]);
            }
        }
    }

    public Item getItemInFire(int index) {
        if (index < 0 || index >= itemsInFire.length) {
            return null;
        }
        return itemsInFire[index];
    }

    public boolean putItemInFire(Item item) {
        if (!(item instanceof ItemEdible)) return false;

        for (int i = 0; i < itemsInFire.length; i++) {
            if (itemsInFire[i] == null) {
                Item food = item.clone();
                if (food.getCount() != 1) food.setCount(1);
                itemsInFire[i] = food;
                cookingTimes[i] = 0;
                spawnToAll();
                this.scheduleUpdate();
                return true;
            }
        }
        return false;
    }

    public boolean putItemInFire(Item item, int index) {
        return this.putItemInFire(item, index, false);
    }

    public boolean putItemInFire(Item item, int index, boolean overwrite) {
        if (index < 0 || index >= itemsInFire.length) return false;
        if (!(item instanceof ItemEdible)) return false;

        Item food = item.clone();
        if (food.getCount() != 1) food.setCount(1);

        boolean addedFood = false;
        if (itemsInFire[index] == null) {
            itemsInFire[index] = food;
            addedFood = true;
        } else if (overwrite) {
            itemsInFire[index] = food;
            cookingTimes[index] = 0;
            addedFood = true;
        }
        if (addedFood) {
            spawnToAll();
            this.scheduleUpdate();
        }
        return addedFood;
    }
}

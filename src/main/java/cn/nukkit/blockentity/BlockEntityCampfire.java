package cn.nukkit.blockentity;

import cn.nukkit.block.BlockCampfire;
import cn.nukkit.block.BlockIds;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemEdible;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Sleepybear
 */
public class BlockEntityCampfire extends BlockEntitySpawnable {
    private Item[] itemsInFire;
    private int[] cookingTimes;

    private static final String[] ITEM_TAGS = {"Item1", "Item2", "Item3", "Item4"};
    private static final String[] TIME_TAGS = {"ItemTime1", "ItemTime2", "ItemTime3", "ItemTime4"};

    public BlockEntityCampfire(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        itemsInFire = new Item[4];
        cookingTimes = new int[4];

        boolean hasItems = false;
        for (int i = 0; i < itemsInFire.length; i++) {
            if (namedTag.contains(ITEM_TAGS[i])) {
                Item item = NBTIO.getItemHelper(namedTag.getCompound(ITEM_TAGS[i]));
                itemsInFire[i] = item;
                hasItems = true;
            } else {
                itemsInFire[i] = null;
            }

            if (namedTag.contains(TIME_TAGS[i])) {
                cookingTimes[i] = namedTag.getInt(TIME_TAGS[i]);
            } else {
                cookingTimes[i] = 0;
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
        for (int i = 0; i < itemsInFire.length; i++) {
            if (itemsInFire[i] != null) {
                tag.put(ITEM_TAGS[i], NBTIO.putItemHelper(itemsInFire[i]));
            }
            tag.putInt(TIME_TAGS[i], cookingTimes[i]);
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
        for (int i = 0; i < itemsInFire.length; i++) {
            if (itemsInFire[i] != null) {
                namedTag.put(ITEM_TAGS[i], NBTIO.putItemHelper(itemsInFire[i]));
                namedTag.putInt(TIME_TAGS[i], cookingTimes[i]);
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

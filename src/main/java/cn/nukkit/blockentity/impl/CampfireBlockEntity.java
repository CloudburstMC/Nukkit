package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockCampfire;
import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.Campfire;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemEdible;
import cn.nukkit.item.ItemUtils;
import cn.nukkit.level.chunk.Chunk;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

/**
 * @author Sleepybear
 */
public class CampfireBlockEntity extends BaseBlockEntity implements Campfire {
    private static final String[] ITEM_TAGS = {"Item1", "Item2", "Item3", "Item4"};
    private static final String[] TIME_TAGS = {"ItemTime1", "ItemTime2", "ItemTime3", "ItemTime4"};
    private final Item[] items = new Item[4];
    private final int[] itemTimes = new int[4];

    public CampfireBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        boolean hasItems = false;
        for (int i = 0; i < 4; i++) {
            if (tag.contains(ITEM_TAGS[i])) {
                Item item = ItemUtils.deserializeItem(tag.getCompound(ITEM_TAGS[i]));
                items[i] = item;
                hasItems = true;
            } else {
                items[i] = null;
            }

            if (tag.contains(TIME_TAGS[i])) {
                itemTimes[i] = tag.getInt(TIME_TAGS[i]);
            } else {
                itemTimes[i] = 0;
            }
        }
        if (hasItems) this.scheduleUpdate();
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        for (int i = 0; i < 4; i++) {
            Item item = this.items[i];
            if (item != null && !item.isNull()) {
                tag.tag(ItemUtils.serializeItem(item).toBuilder().build(ITEM_TAGS[i]));
                tag.intTag(TIME_TAGS[i], this.itemTimes[i]);
            }
        }
    }

    @Override
    public boolean isValid() {
        return getLevel().getBlock(this.getPosition()).getId() == BlockIds.CAMPFIRE;
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
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                if (++itemTimes[i] >= 600) {
                    Item output = getLevel().getServer().getCraftingManager().matchFurnaceRecipe(items[i]).getResult();
                    getLevel().dropItem(this.getPosition(), output);
                    items[i] = null;
                    itemTimes[i] = 0;
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
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                getLevel().dropItem(this.getPosition(), items[i]);
            }
        }
    }

    public Item getItemInFire(int index) {
        if (index < 0 || index >= items.length) {
            return null;
        }
        return items[index];
    }

    public boolean putItemInFire(Item item) {
        if (!(item instanceof ItemEdible)) return false;

        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                Item food = item.clone();
                if (food.getCount() != 1) food.setCount(1);
                items[i] = food;
                itemTimes[i] = 0;
                spawnToAll();
                this.scheduleUpdate();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean putItemInFire(Item item, int index, boolean overwrite) {
        if (index < 0 || index >= items.length) return false;
        if (!(item instanceof ItemEdible)) return false;

        Item food = item.clone();
        if (food.getCount() != 1) food.setCount(1);

        boolean addedFood = false;
        if (items[index] == null) {
            items[index] = food;
            addedFood = true;
        } else if (overwrite) {
            items[index] = food;
            itemTimes[index] = 0;
            addedFood = true;
        }
        if (addedFood) {
            spawnToAll();
            this.scheduleUpdate();
        }
        return addedFood;
    }

    @Override
    public boolean isSpawnable() {
        return true;
    }
}

package cn.nukkit.blockentity;

import cn.nukkit.block.BlockIds;
import cn.nukkit.item.Item;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlockEntityCampfire extends BlockEntitySpawnable {
    private Item[] itemsInFire;
    private int[] cookingTimes;

    public BlockEntityCampfire(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();
        itemsInFire = new Item[4];
        cookingTimes = new int[4];

        for (int i = 1; i <= 4; i++) {
            if (namedTag.contains(("Item" + i))) {
                Item item = NBTIO.getItemHelper(namedTag.getCompound("Item" + i));
                itemsInFire[i - 1] = item;
            } else {
                itemsInFire[i - 1] = null;
            }

            if (namedTag.contains("ItemTime" + i)) {
                cookingTimes[i - 1] = namedTag.getInt("ItemTime" + i);
            } else {
                cookingTimes[i - 1] = 0;
            }
        }

        if (namedTag.contains("isMovable")) {
            movable = namedTag.getByte("isMovable") == 1;
        }
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag().putString("id", BlockEntity.CAMPFIRE).putInt("x", getBlock().x).putInt("y", getBlock().y).putInt("z", getBlock().z);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevel().getBlockAt(this.getX(), this.getY(), this.getZ()).getId() == BlockIds.CAMPFIRE;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        for (int i = 1; i <= 4; i++) {
            if (itemsInFire[i - 1] != null) {
                namedTag.put("Item" + i, NBTIO.putItemHelper(itemsInFire[i - 1]));
                namedTag.putInt("ItemTime" + i, cookingTimes[i - 1]);
            }
            namedTag.putByte("isMovable", movable ? 1 : 0);
        }
    }

    @Override
    public boolean onUpdate() {
        boolean haveUpdate = false;
        for (int i = 0; i < 4; i++) {
            if (itemsInFire[i] != null) {
                if (++cookingTimes[i] >= 600) {
                    Item output = getLevel().getServer().getCraftingManager().matchFurnaceRecipe(itemsInFire[i]).getResult();
                    getLevel().dropItem(this, output);

                    itemsInFire[i] = null;
                    cookingTimes[i] = 0;
                    haveUpdate = true;
                }
            }
        }

        this.lastUpdate = System.currentTimeMillis();
        return haveUpdate;
    }
}

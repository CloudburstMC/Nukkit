package cn.nukkit.blockentity;

import cn.nukkit.item.Item;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.ArrayList;
import java.util.List;

public class BlockEntityCampfire extends BlockEntity {
    private List<Item> itemsInFire;
    private int[] cookingTimes;
    private int[] cookingTotalTimes;

    public BlockEntityCampfire(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        itemsInFire = new ArrayList<>();
        if (nbt.contains("Items")) {
            ListTag<CompoundTag> items = nbt.getList("Items", CompoundTag.class);
            for (CompoundTag tag : items.getAll()) {
                Item i = NBTIO.getItemHelper(tag);
                itemsInFire.add(tag.getByte("Slot"), i);
            }
        }

        if (nbt.contains("CookingTimes")) {
            cookingTimes = nbt.getIntArray("CookingTimes");
        } else {
            cookingTimes = new int[]{0, 0, 0, 0};
        }

        if (nbt.contains("CookingTotalTimes")) {
            cookingTotalTimes = nbt.getIntArray("CookingTotalTimes");
        } else {
            cookingTotalTimes = new int[]{0, 0, 0, 0};
        }
    }

    @Override
    public boolean isBlockEntityValid() {
        return false;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        namedTag.putIntArray("CookingTotalTimes", this.cookingTotalTimes);
        namedTag.putIntArray("CookingTimes", this.cookingTimes);
        if (!itemsInFire.isEmpty()) {
            ListTag<CompoundTag> items = new ListTag<>("Items");
            for (Item item : itemsInFire) {
                items.add(NBTIO.putItemHelper(item, itemsInFire.indexOf(item)));
            }
            namedTag.putList(items);
        }
    }
}

package cn.nukkit.entity.data.entries;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SlotEntityDataEntry implements EntityDataEntry {
    public int id;
    public byte meta;
    public int count;

    public SlotEntityDataEntry() {

    }

    public SlotEntityDataEntry(int id, byte meta, int count) {
        this.id = id;
        this.meta = meta;
        this.count = count;
    }

    public SlotEntityDataEntry(Item item) {
        this.id = item.getId();
        this.meta = (byte) (item.hasMeta() ? item.getDamage() : 0);
        this.count = item.getCount();
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_SLOT;
    }
}

package cn.nukkit.entity.data;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SlotEntityData implements EntityData<Item> {
    public int id;
    public byte meta;
    public int count;

    public SlotEntityData() {

    }

    public SlotEntityData(int id, byte meta, int count) {
        this.id = id;
        this.meta = meta;
        this.count = count;
    }

    public SlotEntityData(Item item) {
        this.id = item.getId();
        this.meta = (byte) (item.hasMeta() ? item.getDamage() : 0);
        this.count = item.getCount();
    }

    @Override
    public Item getData() {
        return new Item(id, meta & 0xff, count);
    }

    @Override
    public void setData(Item data) {
        this.id = data.getId();
        this.meta = (byte) (data.hasMeta() ? data.getDamage() : 0);
        this.count = data.getCount();
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_SLOT;
    }
}

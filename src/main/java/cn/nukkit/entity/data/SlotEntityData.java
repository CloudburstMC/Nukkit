package cn.nukkit.entity.data;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SlotEntityData extends EntityData<Item> {

    public Item item;

    public SlotEntityData(int id, int blockId, int meta, int count) {
        this (id, Item.get(blockId, meta, count));
    }

    public SlotEntityData(int id, Item item) {
        super(id);
        this.item = item;
    }

    @Override
    public Item getData() {
        return this.item;
    }

    @Override
    public void setData(Item data) {
        this.item = data;
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_SLOT;
    }
}

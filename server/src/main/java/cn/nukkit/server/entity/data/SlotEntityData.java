package cn.nukkit.server.entity.data;

import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SlotEntityData extends EntityData<Item> {
    public int blockId;
    public int meta;
    public int count;

    public SlotEntityData(int id, int blockId, int meta, int count) {
        super(id);
        this.blockId = blockId;
        this.meta = meta;
        this.count = count;
    }

    public SlotEntityData(int id, Item item) {
        this(id, item.getId(), (byte) (item.hasMeta() ? item.getDamage() : 0), item.getCount());
    }

    @Override
    public Item getData() {
        return Item.get(blockId, meta, count);
    }

    @Override
    public void setData(Item data) {
        this.blockId = data.getId();
        this.meta = (data.hasMeta() ? data.getDamage() : 0);
        this.count = data.getCount();
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_SLOT;
    }
}

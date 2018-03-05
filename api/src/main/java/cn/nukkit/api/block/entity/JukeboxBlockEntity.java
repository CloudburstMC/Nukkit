package cn.nukkit.api.block.entity;

import cn.nukkit.api.item.ItemType;

public interface JukeboxBlockEntity extends BlockEntity {

    ItemType getRecord();

    /**
     * Set the current record in the jukebox.
     *
     * @param record the record
     * @throws IllegalArgumentException {@link cn.nukkit.api.item.ItemType} must be a record
     */
    void setRecord(ItemType record);
}

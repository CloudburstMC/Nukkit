package cn.nukkit.api.block.entity;

import cn.nukkit.api.item.ItemType;

import javax.annotation.Nullable;
import java.util.Optional;

public interface JukeboxBlockEntity extends BlockEntity {

    Optional<ItemType> getRecord();

    /**
     * Set the current record in the jukebox.
     *
     * @param record the record
     * @throws IllegalArgumentException {@link cn.nukkit.api.item.ItemType} must be a record
     */
    void setRecord(@Nullable ItemType record);
}

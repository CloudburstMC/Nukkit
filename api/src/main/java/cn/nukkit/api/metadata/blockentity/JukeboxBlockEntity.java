package cn.nukkit.api.metadata.blockentity;

import cn.nukkit.api.item.ItemInstance;

import javax.annotation.Nullable;
import java.util.Optional;

public interface JukeboxBlockEntity extends BlockEntity {

    Optional<ItemInstance> getRecord();

    /**
     * Set the current record in the jukebox.
     *
     * @param item the record
     * @throws IllegalArgumentException {@link cn.nukkit.api.item.ItemType} must be a record
     */
    void setRecord(@Nullable ItemInstance item);
}

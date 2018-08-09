package com.nukkitx.server.block.entity;

import com.google.common.base.Preconditions;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.util.ItemTypeUtil;
import com.nukkitx.api.metadata.blockentity.JukeboxBlockEntity;

import javax.annotation.Nullable;
import java.util.Optional;

public class NukkitJukeboxBlockEntity extends NukkitBlockEntity implements JukeboxBlockEntity {
    private ItemInstance record;

    public NukkitJukeboxBlockEntity() {
        super(BlockEntityType.JUKEBOX);
    }

    @Override
    public Optional<ItemInstance> getRecord() {
        return Optional.ofNullable(record);
    }

    @Override
    public void setRecord(@Nullable ItemInstance record) {
        if (!this.record.equals(record)) {
            if (record != null) {
                Preconditions.checkArgument(ItemTypeUtil.isRecord(record.getItemType()), "ItemType must be a record");
            }
            this.record = record;
            // onRecordChange(oldRecord, newRecord);
        }
    }

    public void removeRecord() {

    }

    private void play() {

    }
}

package cn.nukkit.server.block.entity;

import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.util.ItemTypeUtil;
import cn.nukkit.api.metadata.blockentity.JukeboxBlockEntity;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.Optional;

public class NukkitJukeboxBlockEntity implements JukeboxBlockEntity {
    private ItemInstance record;

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

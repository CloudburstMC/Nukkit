package cn.nukkit.server.block.entity;

import cn.nukkit.api.block.entity.JukeboxBlockEntity;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.item.util.ItemTypeUtil;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.Optional;

public class NukkitJukeboxBlockEntity implements JukeboxBlockEntity {
    private ItemType record;

    @Override
    public Optional<ItemType> getRecord() {
        return Optional.ofNullable(record);
    }

    @Override
    public void setRecord(@Nullable ItemType record) {
        Preconditions.checkArgument(ItemTypeUtil.isRecord(record), "ItemType must be a record");
        if (this.record != null) {

        }
        this.record = record;
    }

    public void removeRecord() {

    }

    private void play() {

    }
}

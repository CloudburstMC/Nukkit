package cn.nukkit.api.block.entity;

import cn.nukkit.api.item.component.RecordItemComponent;

public interface JukeboxBlockEntity extends BlockEntity {

    RecordItemComponent getRecordType();

    default
}

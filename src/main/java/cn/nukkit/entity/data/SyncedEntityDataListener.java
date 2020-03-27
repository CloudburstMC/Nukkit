package cn.nukkit.entity.data;

import com.nukkitx.protocol.bedrock.data.EntityDataMap;

public interface SyncedEntityDataListener {

    void onDataChange(EntityDataMap changeSet);
}

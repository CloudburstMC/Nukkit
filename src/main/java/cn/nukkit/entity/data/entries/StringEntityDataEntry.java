package cn.nukkit.entity.data.entries;

import cn.nukkit.entity.Entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class StringEntityDataEntry implements EntityDataEntry {
    public String data;

    public StringEntityDataEntry() {

    }

    public StringEntityDataEntry(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_STRING;
    }
}

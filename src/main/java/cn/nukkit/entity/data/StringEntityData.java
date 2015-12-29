package cn.nukkit.entity.data;

import cn.nukkit.entity.Entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class StringEntityData implements EntityData<String> {
    public String data;

    public StringEntityData() {

    }

    public StringEntityData(String data) {
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

    @Override
    public String toString() {
        return data;
    }
}

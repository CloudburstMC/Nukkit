package cn.nukkit.entity.data;

import cn.nukkit.entity.Entity;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class StringEntityData extends EntityData<String> {

    public String data;

    public StringEntityData(int id, String data) {
        super(id);
        if (data == null) throw new NullPointerException("data must not be null");
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        if (data == null) throw new NullPointerException("data must not be null");
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

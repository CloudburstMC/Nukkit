package cn.nukkit.entity.data;

import java.util.Objects;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityData<T> {
    private int id;

    protected EntityData(int id) {
        this.id = id;
    }

    public abstract int getType();

    public abstract T getData();

    public abstract void setData(T data);

    public int getId() {
        return id;
    }

    public EntityData setId(int id) {
        this.id = id;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EntityData && ((EntityData) obj).getId() == this.getId() && Objects.equals(((EntityData) obj).getData(), this.getData());
    }
}

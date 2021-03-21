package cn.nukkit.nbt.tag;

import java.util.Objects;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class NumberTag<T extends Number> extends Tag {
    protected NumberTag(String name) {
        super(name);
    }

    public abstract T getData();

    public abstract void setData(T data);
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getData());
    }
}

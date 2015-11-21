package cn.nukkit.entity.data.entries;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface EntityDataEntry<T> {

    int getType();

    T getData();

    void setData(T data);
}

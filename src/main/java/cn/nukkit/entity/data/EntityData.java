package cn.nukkit.entity.data;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface EntityData<T> {

    int getType();

    T getData();

    void setData(T data);
}

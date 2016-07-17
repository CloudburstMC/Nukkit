package cn.nukkit.utils;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PriorityObject {
    public final int priority;
    public final Object data;

    public PriorityObject(Object data, int priority) {
        this.data = data;
        this.priority = priority;
    }
}

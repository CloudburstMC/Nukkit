package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Apple extends Item {

    public Apple() {
        this(0, 1);
    }

    public Apple(Integer meta) {
        this(meta, 1);
    }

    public Apple(Integer meta, int count) {
        super(APPLE, 0, count, "Apple");
    }
}

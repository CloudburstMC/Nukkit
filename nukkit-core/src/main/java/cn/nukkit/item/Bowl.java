package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Bowl extends Item {

    public Bowl() {
        this(0, 1);
    }

    public Bowl(Integer meta) {
        this(meta, 1);
    }

    public Bowl(Integer meta, int count) {
        super(BOWL, 0, count, "Bowl");
    }
}

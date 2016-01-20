package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Bread extends EdibleItem {

    public Bread() {
        this(0, 1);
    }

    public Bread(Integer meta) {
        this(meta, 1);
    }

    public Bread(Integer meta, int count) {
        super(BREAD, meta, count, "Bread");
    }
}

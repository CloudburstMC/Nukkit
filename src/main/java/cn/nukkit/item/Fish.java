package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Fish extends Item {

    public Fish() {
        this(0, 1);
    }

    public Fish(Integer meta) {
        this(meta, 1);
    }

    public Fish(Integer meta, int count) {
        super(RAW_FISH, meta, count, "Raw Fish");
    }

    protected Fish(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

}

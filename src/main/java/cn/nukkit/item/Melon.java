package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Melon extends Item {

    public Melon() {
        this(0, 1);
    }

    public Melon(Integer meta) {
        this(meta, 1);
    }

    public Melon(Integer meta, int count) {
        super(MELON, meta, count, "Melon");
    }
}

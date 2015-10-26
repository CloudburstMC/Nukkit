package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Minecart extends Item {

    public Minecart() {
        this(0, 1);
    }

    public Minecart(Integer meta) {
        this(meta, 1);
    }

    public Minecart(Integer meta, int count) {
        super(MINECART, meta, count, "Minecart");
    }
}

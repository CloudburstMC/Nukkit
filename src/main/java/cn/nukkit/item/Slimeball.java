package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Slimeball extends Item {

    public Slimeball() {
        this(0, 1);
    }

    public Slimeball(Integer meta) {
        this(meta, 1);
    }

    public Slimeball(Integer meta, int count) {
        super(SLIMEBALL, meta, count, "Slimeball");
    }
}

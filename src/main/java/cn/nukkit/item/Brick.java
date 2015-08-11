package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Brick extends Item {

    public Brick() {
        this(0, 1);
    }

    public Brick(int meta) {
        this(meta, 1);
    }

    public Brick(int meta, int count) {
        super(BRICK, 0, count, "Brick");
    }
}

package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Brick extends Item {

    public Brick() {
        this(0, 1);
    }

    public Brick(Integer meta) {
        this(meta, 1);
    }

    public Brick(Integer meta, int count) {
        super(BRICK, 0, count, "Brick");
    }
}

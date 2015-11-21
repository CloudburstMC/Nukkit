package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NetherBrick extends Item {

    public NetherBrick() {
        this(0, 1);
    }

    public NetherBrick(Integer meta) {
        this(meta, 1);
    }

    public NetherBrick(Integer meta, int count) {
        super(NETHER_BRICK, meta, count, "Nether Brick");
    }
}

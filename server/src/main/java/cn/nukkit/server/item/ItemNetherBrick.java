package cn.nukkit.server.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemNetherBrick extends Item {

    public ItemNetherBrick() {
        this(0, 1);
    }

    public ItemNetherBrick(Integer meta) {
        this(meta, 1);
    }

    public ItemNetherBrick(Integer meta, int count) {
        super(NETHER_BRICK, meta, count, "Nether Brick");
    }
}

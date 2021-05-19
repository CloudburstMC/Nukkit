package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBrick extends Item {

    public ItemBrick() {
        this(0, 1);
    }

    public ItemBrick(Integer meta) {
        this(meta, 1);
    }

    public ItemBrick(Integer meta, int count) {
        super(BRICK, 0, count, "Brick");
    }
}

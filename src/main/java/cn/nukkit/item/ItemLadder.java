package cn.nukkit.item;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ItemLadder extends Item {

    public ItemLadder() {
        this(0, 1);
    }

    public ItemLadder(Integer meta) {
        this(meta, 1);
    }

    public ItemLadder(Integer meta, int count) {
        super(LADDER, 0, count, "Ladder");
    }
}

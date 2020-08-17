package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemPaper extends Item {

    public ItemPaper() {
        this(0, 1);
    }

    public ItemPaper(Integer meta) {
        this(meta, 1);
    }

    public ItemPaper(Integer meta, int count) {
        super(PAPER, meta, count, "Paper");
    }
}

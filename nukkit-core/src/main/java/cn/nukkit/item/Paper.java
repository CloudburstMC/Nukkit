package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Paper extends Item {

    public Paper() {
        this(0, 1);
    }

    public Paper(Integer meta) {
        this(meta, 1);
    }

    public Paper(Integer meta, int count) {
        super(PAPER, meta, count, "Paper");
    }
}

package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Coal extends Item {

    public Coal() {
        this(0, 1);
    }

    public Coal(Integer meta) {
        this(meta, 1);
    }

    public Coal(Integer meta, int count) {
        super(COAL, meta, count, "Coal");
        if (this.meta == 1) {
            this.name = "Charcoal";
        }
    }
}

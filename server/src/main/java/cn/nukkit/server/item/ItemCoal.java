package cn.nukkit.server.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemCoal extends Item {

    public ItemCoal() {
        this(0, 1);
    }

    public ItemCoal(Integer meta) {
        this(meta, 1);
    }

    public ItemCoal(Integer meta, int count) {
        super(COAL, meta, count, "Coal");
        if (this.meta == 1) {
            this.name = "Charcoal";
        }
    }
}

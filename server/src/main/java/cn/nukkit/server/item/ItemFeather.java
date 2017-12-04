package cn.nukkit.server.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemFeather extends Item {

    public ItemFeather() {
        this(0, 1);
    }

    public ItemFeather(Integer meta) {
        this(meta, 1);
    }

    public ItemFeather(Integer meta, int count) {
        super(FEATHER, 0, count, "Feather");
    }
}

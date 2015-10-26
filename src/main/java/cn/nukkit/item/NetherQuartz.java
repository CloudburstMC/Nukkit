package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class NetherQuartz extends Item {

    public NetherQuartz() {
        this(0, 1);
    }

    public NetherQuartz(Integer meta) {
        this(meta, 1);
    }

    public NetherQuartz(Integer meta, int count) {
        super(NETHER_QUARTZ, 0, count, "Nether Quartz");
    }
}

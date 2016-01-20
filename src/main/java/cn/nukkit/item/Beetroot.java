package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Beetroot extends EdibleItem {

    public Beetroot() {
        this(0, 1);
    }

    public Beetroot(Integer meta) {
        this(meta, 1);
    }

    public Beetroot(Integer meta, int count) {
        super(BEETROOT, meta, count, "Beetroot");
    }

}

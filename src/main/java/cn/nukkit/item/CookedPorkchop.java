package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CookedPorkchop extends EdibleItem {

    public CookedPorkchop() {
        this(0, 1);
    }

    public CookedPorkchop(Integer meta) {
        this(meta, 1);
    }

    public CookedPorkchop(Integer meta, int count) {
        super(COOKED_PORKCHOP, meta, count, "Cooked Porkchop");
    }
}

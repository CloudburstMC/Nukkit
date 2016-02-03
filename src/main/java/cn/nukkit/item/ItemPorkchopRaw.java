package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemPorkchopRaw extends ItemEdible {

    public ItemPorkchopRaw() {
        this(0, 1);
    }

    public ItemPorkchopRaw(Integer meta) {
        this(meta, 1);
    }

    public ItemPorkchopRaw(Integer meta, int count) {
        super(RAW_PORKCHOP, meta, count, "Raw Porkchop");
    }
}

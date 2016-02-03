package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemPorkchopCooked extends ItemEdible {

    public ItemPorkchopCooked() {
        this(0, 1);
    }

    public ItemPorkchopCooked(Integer meta) {
        this(meta, 1);
    }

    public ItemPorkchopCooked(Integer meta, int count) {
        super(COOKED_PORKCHOP, meta, count, "Cooked Porkchop");
    }
}

package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
abstract public class Armor extends Item {

    public Armor(int id) {
        super(id);
    }

    public Armor(int id, int meta) {
        super(id, meta);
    }

    public Armor(int id, int meta, int count) {
        super(id, meta, count);
    }

    public Armor(int id, int meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

}

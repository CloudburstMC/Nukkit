package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EdibleItem extends Item {
    public EdibleItem(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    public EdibleItem(int id) {
        super(id);
    }

    public EdibleItem(int id, Integer meta) {
        super(id, meta);
    }

    public EdibleItem(int id, Integer meta, int count) {
        super(id, meta, count);
    }

}

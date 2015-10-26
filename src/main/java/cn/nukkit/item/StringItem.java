package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class StringItem extends Item {

    public StringItem() {
        this(0, 1);
    }

    public StringItem(Integer meta) {
        this(meta, 1);
    }

    public StringItem(Integer meta, int count) {
        super(STRING, meta, count, "String");
    }
}

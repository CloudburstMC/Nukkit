package cn.nukkit.item;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class GoldenAppleEnchanted extends EdibleItem {
    public GoldenAppleEnchanted() {
        this(0, 1);
    }

    public GoldenAppleEnchanted(Integer meta) {
        this(meta, 1);
    }

    public GoldenAppleEnchanted(Integer meta, int count) {
        super(GOLDEN_APPLE_ENCHANTED, meta, count, "Enchanted Golden Apple");
    }
}

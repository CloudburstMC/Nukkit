package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BookEnchanted extends Item {

    public BookEnchanted() {
        this(0, 1);
    }

    public BookEnchanted(Integer meta) {
        this(meta, 1);
    }

    public BookEnchanted(Integer meta, int count) {
        super(ENCHANTED_BOOK, meta, count, "Enchanted Book");
    }
}

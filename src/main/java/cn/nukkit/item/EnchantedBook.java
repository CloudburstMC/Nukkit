package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantedBook extends Item {

    public EnchantedBook() {
        this(0, 1);
    }

    public EnchantedBook(Integer meta) {
        this(meta, 1);
    }

    public EnchantedBook(Integer meta, int count) {
        super(ENCHANTED_BOOK, meta, count, "Enchanted Book");
    }
}

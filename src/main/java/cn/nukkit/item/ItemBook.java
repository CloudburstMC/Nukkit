package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemBook extends Item {

    public ItemBook() {
        this(0, 1);
    }

    public ItemBook(Integer meta) {
        this(meta, 1);
    }

    public ItemBook(Integer meta, int count) {
        super(BOOK, meta, count, "Book");
    }

    @Override
    public int getEnchantAbility() {
        return 1;
    }
}

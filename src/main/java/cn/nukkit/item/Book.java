package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Book extends Item {

    public Book() {
        this(0, 1);
    }

    public Book(Integer meta) {
        this(meta, 1);
    }

    public Book(Integer meta, int count) {
        super(BOOK, meta, count, "Book");
    }
}

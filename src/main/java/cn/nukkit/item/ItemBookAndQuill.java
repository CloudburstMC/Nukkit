package cn.nukkit.item;

public class ItemBookAndQuill extends ItemBookWritable {

    public ItemBookAndQuill() {
        this(0, 1);
    }

    public ItemBookAndQuill(Integer meta) {
        this(meta, 1);
    }

    public ItemBookAndQuill(Integer meta, int count) {
        super(Item.BOOK_AND_QUILL, 0, count, "Book & Quill");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}

package cn.nukkit.item;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

public class ItemBookWritten extends ItemBookWritable {

    public static final int GENERATION_ORIGINAL = 0;
    public static final int GENERATION_COPY = 1;
    public static final int GENERATION_COPY_OF_COPY = 2;
    public static final int GENERATION_TATTERED = 3;

    public ItemBookWritten() {
        this(0, 1);
    }

    public ItemBookWritten(Integer meta) {
        this(meta, 1);
    }

    public ItemBookWritten(Integer meta, int count) {
        super(Item.WRITTEN_BOOK, 0, count, "Written Book");
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    public Item writeBook(String author, String title, String[] pages) {
        ListTag<CompoundTag> pageList = new ListTag<>("pages");
        for (String page : pages) {
            pageList.add(createPageTag(page));
        }
        return writeBook(author, title, pageList);
    }

    public Item writeBook(String author, String title, ListTag<CompoundTag> pages) {
        if (pages.size() > 50 || pages.size() <= 0) return this; //Minecraft does not support more than 50 pages
        CompoundTag tag = this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag();

        tag.putString("author", author);
        tag.putString("title", title);
        tag.putList(pages);

        tag.putInt("generation", GENERATION_ORIGINAL);
        tag.putString("xuid", "");

        return this.setNamedTag(tag);
    }

    public boolean signBook(String title, String author, String xuid, int generation) {
        this.setNamedTag((this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag())
                .putString("title", title)
                .putString("author", author)
                .putInt("generation", generation)
                .putString("xuid", xuid));
        return true;
    }

    /**
     * Returns the generation of the book.
     * Generations higher than 1 can not be copied.
     */
    public int getGeneration() {
        return this.hasCompoundTag() ? this.getNamedTag().getInt("generation") : -1;
    }

    /**
     * Sets the generation of a book.
     */
    public void setGeneration(int generation) {
        this.setNamedTag((this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag()).putInt("generation", generation));
    }

    /**
     * Returns the author of this book.
     * This is not a reliable way to get the name of the player who signed this book.
     * The author can be set to anything when signing a book.
     */
    public String getAuthor() {
        return this.hasCompoundTag() ? this.getNamedTag().getString("author") : "";
    }

    /**
     * Sets the author of this book.
     */
    public void setAuthor(String author) {
        this.setNamedTag((this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag()).putString("author", author));
    }

    /**
     * Returns the title of this book.
     */
    public String getTitle() {
        return this.hasCompoundTag() ? this.getNamedTag().getString("title") : "Written Book";
    }

    /**
     * Sets the title of this book.
     */
    public void setTitle(String title) {
        this.setNamedTag((this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag()).putString("title", title));
    }

    /**
     * Returns the author's XUID of this book.
     */
    public String getXUID() {
        return this.hasCompoundTag() ? this.getNamedTag().getString("xuid") : "";
    }

    /**
     * Sets the author's XUID of this book.
     */
    public void setXUID(String title) {
        this.setNamedTag((this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag()).putString("xuid", title));
    }
}

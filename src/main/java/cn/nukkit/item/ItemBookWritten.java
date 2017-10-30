package cn.nukkit.item;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.concurrent.ThreadLocalRandom;

public class ItemBookWritten extends Item {

    protected boolean isWritten = false;

    public ItemBookWritten() {
        this(0, 1);
    }

    public ItemBookWritten(Integer meta, int count) {
        super(Item.WRITTEN_BOOK, 0, count, "Book");
    }

    public Item writeBook(String author, String title, String[] pages) {
        ListTag<CompoundTag> pageList = new ListTag<>("pages");
        for (String page : pages) {
            pageList.add(new CompoundTag().putString("photoname", "").putString("text", page));
        }
        return writeBook(author, title, pageList);
    }

    public Item writeBook(String author, String title, ListTag<CompoundTag> pages) {
        if (pages.size() > 50 || pages.size() <= 0) return this; //Minecraft does not support more than 50 pages
        if (this.isWritten) return this; //Book content can only be updated once
        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }

        tag.putString("author", author);
        tag.putString("title", title);
        tag.putList(pages);

        tag.putInt("generation", 0);
        long randomId = 1095216660480L + ThreadLocalRandom.current().nextLong(0L, 2147483647L);
        tag.putLong("id", randomId);

        this.isWritten = true;
        return this.setNamedTag(tag);
    }

    public String getAuthor() {
        if (!this.isWritten) return "";
        return this.getNamedTag().getString("author");
    }

    public String getTitle() {
        if (!this.isWritten) return "Book";
        return this.getNamedTag().getString("title");
    }

    public String[] getPages() {
        if (!this.isWritten) return new String[0];
        ListTag<CompoundTag> tag = (ListTag<CompoundTag>) this.getNamedTag().getList("pages");
        String[] pages = new String[tag.size()];
        int i = 0;
        for (CompoundTag pageCompound : tag.getAll()) {
            pages[i] = pageCompound.getString("text");
            i++;
        }
        return pages;
    }
}
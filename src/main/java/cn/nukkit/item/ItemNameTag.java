package cn.nukkit.item;

public class ItemNameTag extends Item {

    public ItemNameTag() {
        this(0, 1);
    }

    public ItemNameTag(Integer meta) {
        this(meta, 1);
    }

    public ItemNameTag(Integer meta, int count) {
        super(NAME_TAG, meta, count, "Name Tag");
    }

}

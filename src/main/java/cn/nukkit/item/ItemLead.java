package cn.nukkit.item;

public class ItemLead extends Item {

    public ItemLead() {
        this(0, 1);
    }

    public ItemLead(Integer meta) {
        this(meta, 1);
    }

    public ItemLead(Integer meta, int count) {
        super(LEAD, 0, count, "Lead");
    }
}

package cn.nukkit.server.item;

public class ItemEndCrystal extends Item {

    public ItemEndCrystal() {
        this(0, 1);
    }

    public ItemEndCrystal(Integer meta) {
        this(meta, 1);
    }

    public ItemEndCrystal(Integer meta, int count) {
        super(END_CRYSTAL, meta, count, "End Crystal");
    }
}

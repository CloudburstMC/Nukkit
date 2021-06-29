package cn.nukkit.item;

public class ItemPhantomMembrane extends Item {

    public ItemPhantomMembrane() {
        this(0, 1);
    }

    public ItemPhantomMembrane(Integer meta) {
        this(meta, 1);
    }

    public ItemPhantomMembrane(Integer meta, int count) {
        super(PHANTOM_MEMBRANE, meta, count, "Phantom Membrane");
    }
}

package cn.nukkit.item;

public class ItemShulkerShell extends Item {

    public ItemShulkerShell() {
        this(0, 1);
    }

    public ItemShulkerShell(Integer meta) {
        this(meta, 1);
    }

    public ItemShulkerShell(Integer meta, int count) {
        super(SHULKER_SHELL, 0, count, "Shulker Shell");
    }
}

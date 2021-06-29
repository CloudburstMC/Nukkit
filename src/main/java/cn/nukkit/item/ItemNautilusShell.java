package cn.nukkit.item;

public class ItemNautilusShell extends Item {

    public ItemNautilusShell() {
        this(0, 1);
    }

    public ItemNautilusShell(Integer meta) {
        this(meta, 1);
    }

    public ItemNautilusShell(Integer meta, int count) {
        super(NAUTILUS_SHELL, meta, count, "Nautilus Shell");
    }
}

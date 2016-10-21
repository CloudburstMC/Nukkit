package cn.nukkit.item;

public class ItemSaddle extends Item {
    public ItemSaddle() {
        this(0, 0);
    }

    public ItemSaddle(Integer meta) {
        this(meta, 0);
    }

    public ItemSaddle(Integer meta, int count) {
        super(SADDLE, meta, count, "Saddle");
    }
}

package cn.nukkit.item;

public class ItemAmethystShard extends Item {

    public ItemAmethystShard() {
        this(0, 1);
    }

    public ItemAmethystShard(Integer meta) {
        this(meta, 1);
    }

    public ItemAmethystShard(Integer meta, int count) {
        super(AMETHYST_SHARD, meta, count, "Amethyst Shard");
    }
}

package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockInfestedDeepslate extends BlockDeepslate {

    public BlockInfestedDeepslate() {
        this(0);
    }

    public BlockInfestedDeepslate(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return INFESTED_DEEPSLATE;
    }

    @Override
    public String getName() {
        return "Infested Deepslate";
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0.75;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }
}

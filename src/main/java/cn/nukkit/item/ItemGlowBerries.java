package cn.nukkit.item;

import cn.nukkit.block.Block;

public class ItemGlowBerries extends ItemEdible {

    public ItemGlowBerries() {
        this(0, 1);
    }

    public ItemGlowBerries(Integer meta) {
        this(meta, 1);
    }

    public ItemGlowBerries(Integer meta, int count) {
        super(GLOW_BERRIES, meta, count, "Glow Berries");
        this.block = Block.get(CAVE_VINES);
    }
}

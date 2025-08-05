package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ItemRedstone extends Item implements ItemTrimMaterial {

    public ItemRedstone() {
        this(0, 1);
    }

    public ItemRedstone(Integer meta) {
        this(meta, 1);
    }

    public ItemRedstone(Integer meta, int count) {
        super(REDSTONE, meta, count, "Redstone Dust");
        this.block = Block.get(REDSTONE_WIRE);
    }

    @Override
    public ItemTrimMaterial.Type getMaterial() {
        return ItemTrimMaterial.Type.REDSTONE;
    }
}

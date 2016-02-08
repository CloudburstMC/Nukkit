package cn.nukkit.item;

import cn.nukkit.block.BlockPotato;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemPotato extends ItemEdible {

    public ItemPotato() {
        this(0, 1);
    }

    public ItemPotato(Integer meta) {
        this(meta, 1);
    }

    public ItemPotato(Integer meta, int count) {
        super(POTATO, meta, count, "Potato");
        this.block = new BlockPotato();
    }

    protected ItemPotato(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }
}

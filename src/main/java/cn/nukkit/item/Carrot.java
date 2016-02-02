package cn.nukkit.item;

import cn.nukkit.block.BlockCarrot;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Carrot extends EdibleItem {

    public Carrot() {
        this(0, 1);
    }

    public Carrot(Integer meta) {
        this(meta, 1);
    }

    public Carrot(Integer meta, int count) {
        super(CARROT, 0, count, "Carrot");
        this.block = new BlockCarrot();
    }

}

package cn.nukkit.item;

import cn.nukkit.block.BlockKelp;

public class ItemKelp extends Item {
    
    public ItemKelp() {
        this(0, 1);
    }
    
    public ItemKelp(Integer meta) {
        this(meta, 1);
    }
    
    public ItemKelp(Integer meta, int count) {
        super(KELP, meta, count, "Kelp");
        this.block = new BlockKelp();
    }
    
}

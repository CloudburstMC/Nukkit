package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Air;
import cn.nukkit.block.Block;
import cn.nukkit.block.RedstoneDust;
import cn.nukkit.block.Transparent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.level.Level;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Redstone extends Item {

    public Redstone() {
        this(0, 1);
    }

    public Redstone(Integer meta) {
        this(meta, 1);
    }

    public Redstone(Integer meta, int count) {
        super(REDSTONE, meta, count, "Redstone");
        this.block = Block.get(Item.REDSTONE_DUST_BLOCK);
    }

}

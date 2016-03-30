package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.level.Level;

/**
 * Created by Pub4Game on 30.03.2016.
 */
public class BlockLitRedstoneLamp extends BlockRedstoneLamp {

    public BlockLitRedstoneLamp(int meta) {
        super(meta);
    }

    public BlockLitRedstoneLamp() {
        this(0);
    }

    @Override
    public String getName() {
        return "Lit Redstone Lamp";
    }

    @Override
    public int getId() {
        return LIT_REDSTONE_LAMP;
    }

    @Override
    public int onUpdate(int type) {
        if ((type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_SCHEDULED) && this.getNeighborPowerLevel() <= 0) {
            this.getLevel().setBlock(this, new BlockRedstoneLamp());
        }
        return 0;
    }
    
    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.REDSTONE_LAMP, 0, 1}
        };
    }
}

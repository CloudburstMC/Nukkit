package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockConcretePowder extends BlockFallable {
    private int meta;

    public BlockConcretePowder() {
        this(0);
    }

    public BlockConcretePowder(int meta) {
        this.meta = meta;
    }

    @Override
    public int getFullId() {
        return (getId() << 4) + getDamage();
    }

    @Override
    public final int getDamage() {
        return this.meta;
    }

    @Override
    public final void setDamage(int meta) {
        this.meta = meta;
    }

    @Override
    public int getId() {
        return CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return "Concrete Powder";
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }
    
    @Override
    public int onUpdate(int type) {
        super.onUpdate(Level.BLOCK_UPDATE_NORMAL); //#BlockFallable
        if(type == Level.BLOCK_UPDATE_NORMAL){
            for(int side = 1; side <= 5; side++){
                Block block = this.getSide(BlockFace.fromIndex(side));
                if(block.getId() == Block.WATER || block.getId() == Block.STILL_WATER){
                    this.level.setBlock(this, Block.get(Block.CONCRETE, this.meta));
                }
            }
            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }
}

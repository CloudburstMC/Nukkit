package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.block.BlockIds.*;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockConcretePowder extends BlockFallable {

    public BlockConcretePowder(Identifier id) {
        super(id);
    }

    @Override
    public float getResistance() {
        return 2.5f;
    }

    @Override
    public float getHardness() {
        return 0.5f;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }
    
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            super.onUpdate(Level.BLOCK_UPDATE_NORMAL);

            for (int side = 1; side <= 5; side++) {
                Block block = this.getSide(BlockFace.fromIndex(side));
                if (block.getId() == FLOWING_WATER || block.getId() == WATER || block.getId() == FLOWING_LAVA || block.getId() == LAVA) {
                    this.level.setBlock(this.getPosition(), Block.get(CONCRETE, this.meta), true, true);
                }
            }

            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block b, Block target, BlockFace face, Vector3f clickPos, Player player) {
        boolean concrete = false;

        for (int side = 1; side <= 5; side++) {
            Block block = this.getSide(BlockFace.fromIndex(side));
            if (block.getId() == FLOWING_WATER || block.getId() == WATER || block.getId() == FLOWING_LAVA || block.getId() == LAVA) {
                concrete = true;
                break;
            }
        }

        if (concrete) {
            this.level.setBlock(this.getPosition(), Block.get(CONCRETE, this.getMeta()), true, true);
        } else {
            this.level.setBlock(this.getPosition(), this, true, true);
        }

        return true;
    }
}

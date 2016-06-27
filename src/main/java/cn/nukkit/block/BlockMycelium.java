package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.event.block.BlockSpreadEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

/**
 * Created by Pub4Game on 03.01.2016.
 */
public class BlockMycelium extends BlockSolid {

    public BlockMycelium() {
        this(0);
    }

    public BlockMycelium(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Mycelium";
    }

    @Override
    public int getId() {
        return MYCELIUM;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.DIRT, 0, 1}
        };
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            //TODO: light levels
            NukkitRandom random = new NukkitRandom();
            x = random.nextRange((int) x - 1, (int) x + 1);
            y = random.nextRange((int) y - 1, (int) y + 1);
            z = random.nextRange((int) z - 1, (int) z + 1);
            Block block = this.getLevel().getBlock(new Vector3(x, y, z));
            if (block.getId() == Block.DIRT) {
                if (block.getSide(1) instanceof BlockTransparent) {
                    BlockSpreadEvent ev = new BlockSpreadEvent(block, this, new BlockMycelium());
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(block, ev.getNewState());
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRASS_BLOCK_COLOR;
    }
}

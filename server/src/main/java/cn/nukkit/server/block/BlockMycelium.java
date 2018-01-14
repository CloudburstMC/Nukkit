package cn.nukkit.server.block;

import cn.nukkit.api.event.block.BlockSpreadEvent;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemBlock;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.math.NukkitRandom;
import cn.nukkit.server.math.Vector3;
import cn.nukkit.server.util.BlockColor;

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
    public double getResistance() {
        return 2.5;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                new ItemBlock(new BlockDirt())
        };
    }

    @Override
    public int onUpdate(int type) {
        if (type == NukkitLevel.BLOCK_UPDATE_RANDOM) {
            //TODO: light levels
            NukkitRandom random = new NukkitRandom();
            x = random.nextRange((int) x - 1, (int) x + 1);
            y = random.nextRange((int) y - 1, (int) y + 1);
            z = random.nextRange((int) z - 1, (int) z + 1);
            Block block = this.getLevel().getBlock(new Vector3(x, y, z));
            if (block.getId() == Block.DIRT) {
                if (block.up() instanceof BlockTransparent) {
                    BlockSpreadEvent ev = new BlockSpreadEvent(block, this, new BlockMycelium());
                    NukkitServer.getInstance().getPluginManager().callEvent(ev);
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

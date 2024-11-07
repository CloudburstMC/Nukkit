package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.utils.Utils;

/**
 * Created by Pub4Game on 15.01.2016.
 */
public class BlockStemPumpkin extends BlockCrops {

    public BlockStemPumpkin() {
        this(0);
    }

    public BlockStemPumpkin(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PUMPKIN_STEM;
    }

    @Override
    public String getName() {
        return "Pumpkin Stem";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() != FARMLAND) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (Utils.rand()) {
                if (this.getDamage() < 0x07) {
                    Block block = this.clone();
                    block.setDamage(block.getDamage() + 1);
                    BlockGrowEvent ev = new BlockGrowEvent(this, block);
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(this, ev.getNewState(), true);
                    }
                    return Level.BLOCK_UPDATE_RANDOM;
                } else {
                    for (BlockFace face : Plane.HORIZONTAL) {
                        Block b = this.getSide(face);
                        if (b.getId() == PUMPKIN) {
                            return Level.BLOCK_UPDATE_RANDOM;
                        }
                    }
                    Block side = this.getSide(Plane.HORIZONTAL.random());
                    Block d;
                    if (side.getId() == AIR && ((d = side.down()).getId() == FARMLAND || d.getId() == GRASS || d.getId() == DIRT)) {
                        BlockGrowEvent ev = new BlockGrowEvent(side, Block.get(PUMPKIN));
                        Server.getInstance().getPluginManager().callEvent(ev);
                        if (!ev.isCancelled()) {
                            this.getLevel().setBlock(side, ev.getNewState(), true);
                        }
                    }
                }
            }
            return Level.BLOCK_UPDATE_RANDOM;
        }
        return 0;
    }

    @Override
    public Item toItem() {
        return Item.get(Item.PUMPKIN_SEEDS);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (this.getDamage() < 4) return new Item[0];
        return new Item[]{
                Item.get(Item.PUMPKIN_SEEDS, 0, Utils.rand(0, 48) >> 4)
        };
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }
}

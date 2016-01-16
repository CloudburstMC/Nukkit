package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

/**
 * Created by Pub4Game on 09.01.2016.
 */
public class Sugarcane extends Flowable {

    public Sugarcane() {
        this(0);
    }

    public Sugarcane(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Sugarcane";
    }

    @Override
    public int getId() {
        return Block.SUGARCANE_BLOCK;
    }


    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{new int[]{Item.SUGARCANE, 0, 1}};
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == Item.DYE && item.getDamage() == 0x0F) { //Bonemeal
            if (this.getSide(0).getId() != SUGARCANE_BLOCK) {
                for (y = 1; y < 3; ++y) {
                    Block b = this.getLevel().getBlock(new Vector3(this.x, this.y + y, this.z));
                    if (b.getId() == AIR) {
                        BlockGrowEvent ev = new BlockGrowEvent(b, new Sugarcane());
                        Server.getInstance().getPluginManager().callEvent(ev);
                        if (!ev.isCancelled()) {
                            this.getLevel().setBlock(b, ev.getNewState(), true);
                        }
                        break;
                    }
                }
                this.meta = 0;
                this.getLevel().setBlock(this, this, true);
            }
            if ((player.gamemode & 0x01) == 0) {
                item.count--;
            }
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = this.getSide(0);
            if (down.isTransparent() && down.getId() != SUGARCANE_BLOCK) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (this.getSide(0).getId() != SUGARCANE_BLOCK) {
                if (this.meta == 0x0F) {
                    for (y = 1; y < 3; ++y) {
                        Block b = this.getLevel().getBlock(new Vector3(this.x, this.y + y, this.z));
                        if (b.getId() == AIR) {
                            this.getLevel().setBlock(b, new Sugarcane(), true);
                            break;
                        }
                    }
                    this.meta = 0;
                    this.getLevel().setBlock(this, this, true);
                } else {
                    ++this.meta;
                    this.getLevel().setBlock(this, this, true);
                }
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        Block down = this.getSide(0);
        if (down.getId() == SUGARCANE_BLOCK) {
            this.getLevel().setBlock(block, new Sugarcane(), true);
            return true;
        } else if (down.getId() == GRASS || down.getId() == DIRT || down.getId() == SAND) {
            Block block0 = down.getSide(2);
            Block block1 = down.getSide(3);
            Block block2 = down.getSide(4);
            Block block3 = down.getSide(5);
            if ((block0 instanceof Water) || (block1 instanceof Water) || (block2 instanceof Water) || (block3 instanceof Water)) {
                this.getLevel().setBlock(block, new Sugarcane(), true);
                return true;
            }
        }
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}

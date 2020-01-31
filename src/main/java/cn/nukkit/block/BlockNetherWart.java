package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemNetherWart;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.Random;

/**
 * Created by Leonidius20 on 22.03.17.
 */
public class BlockNetherWart extends BlockFlowable {

    public BlockNetherWart() {
        this(0);
    }

    public BlockNetherWart(int meta) {
        super(meta);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        if (down.getId() == SOUL_SAND) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() != SOUL_SAND) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (new Random().nextInt(10) == 1) {
                if (this.getDamage() < 0x03) {
                    BlockNetherWart block = (BlockNetherWart) this.clone();
                    block.setDamage(block.getDamage() + 1);
                    BlockGrowEvent ev = new BlockGrowEvent(this, block);
                    Server.getInstance().getPluginManager().callEvent(ev);

                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(this, ev.getNewState(), true, true);
                    } else {
                        return Level.BLOCK_UPDATE_RANDOM;
                    }
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }

        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    public String getName() {
        return "Nether Wart Block";
    }

    @Override
    public int getId() {
        return NETHER_WART_BLOCK;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (this.getDamage() == 0x03) {
            return new Item[]{
                    new ItemNetherWart(0, 2 + (int) (Math.random() * ((4 - 2) + 1)))
            };
        } else {
            return new Item[]{
                    new ItemNetherWart()
            };
        }
    }

    @Override
    public Item toItem() {
        return new ItemNetherWart();
    }
}



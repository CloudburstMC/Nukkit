package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;

import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Crops extends Flowable {
    public Crops(int id) {
        super(id);
    }

    public Crops(int id, int meta) {
        super(id, meta);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz) {
        return this.place(item, block, target, face, fx, fy, fz, null);
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        Block down = this.getSide(0);
        if (down.getId() == FARMLAND) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean onActivate(Item item) {
        //骨粉
        if (item.getId() == Item.DYE && item.getDamage() == 0x0f) {
            Crops block = (Crops) this.clone();
            block.meta += new Random().nextInt(3) + 2;
            if (block.meta > 7) {
                block.meta = 7;
            }
            BlockGrowEvent ev = new BlockGrowEvent(this, block);
            Server.getInstance().getPluginManager().callEvent(ev);

            if (!ev.isCancelled()) {
                this.getLevel().setBlock(this, ev.getNewState(), true, true);
            }

            item.count--;

            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getSide(0).isTransparent()) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (new Random().nextInt(2) == 1) {
                if (this.meta < 0x07) {
                    Crops block = (Crops) this.clone();
                    ++block.meta;
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
}

package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemStick;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.util.BlockColor;

import java.util.Random;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockDeadBush extends BlockFlowable {
    public BlockDeadBush() {
        this(0);
    }

    public BlockDeadBush(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Dead Bush";
    }

    @Override
    public int getId() {
        return DEAD_BUSH;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        if (down.getId() == SAND || down.getId() == TERRACOTTA || down.getId() == STAINED_TERRACOTTA || down.getId() == PODZOL) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }


    @Override
    public int onUpdate(int type) {
        if (type == NukkitLevel.BLOCK_UPDATE_NORMAL) {
            if (this.down().isTransparent()) {
                this.getLevel().useBreakOn(this);

                return NukkitLevel.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{
                    toItem()
            };
        } else {
            return new Item[]{
                    new ItemStick(0, new Random().nextInt(3))
            };
        }
    }

    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}

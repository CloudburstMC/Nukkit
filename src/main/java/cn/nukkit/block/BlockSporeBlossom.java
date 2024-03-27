package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

public class BlockSporeBlossom extends BlockTransparent {

    public BlockSporeBlossom() {
    }

    @Override
    public int getId() {
        return SPORE_BLOSSOM;
    }

    @Override
    public String getName() {
        return "Spore Blossom";
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block.up().isSolid()) {
            return super.place(item, block, target, face, fx, fy, fz, player);
        }
        return false;
    }

    @Override
    public double getMinX() {
        return this.x + 2D / 16D;
    }

    @Override
    public double getMinY() {
        return this.y + 13D / 16D;
    }

    @Override
    public double getMinZ() {
        return this.z + 2D / 16D;
    }

    @Override
    public double getMaxX() {
        return this.x + 14D / 16D;
    }

    @Override
    public double getMaxZ() {
        return this.z + 14D / 16D;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.getLevel().useBreakOn(this, null, null, true);
        } else if (type == Level.BLOCK_UPDATE_NORMAL && !this.up().isSolid()) {
            this.getLevel().scheduleUpdate(this, 1);
        }
        return type;
    }
}

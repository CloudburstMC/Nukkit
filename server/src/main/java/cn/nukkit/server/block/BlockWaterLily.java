package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemBlock;
import cn.nukkit.server.level.Level;
import cn.nukkit.server.math.AxisAlignedBB;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.utils.BlockColor;

/**
 * Created on 2015/12/1 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockWaterLily extends BlockFlowable {

    public BlockWaterLily() {
        this(0);
    }

    public BlockWaterLily(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Lily Pad";
    }

    @Override
    public int getId() {
        return WATER_LILY;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new AxisAlignedBB(
                this.x + 0.0625,
                this.y,
                this.z + 0.0625,
                this.x + 0.9375,
                this.y + 0.015625,
                this.z + 0.9375
        );
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (target instanceof BlockWater) {
            Block up = target.up();
            if (up.getId() == Block.AIR) {
                this.getLevel().setBlock(up, this, true, true);
                return true;
            }
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!(this.down() instanceof BlockWater)) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }
}

package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/8 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockLadder extends BlockTransparent {

    public BlockLadder() {
        this(0);
    }

    public BlockLadder(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Ladder";
    }

    @Override
    public int getId() {
        return LADDER;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
        entity.onGround = true;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {

        double f = 0.125d;

        if (this.meta == 2) {
            return new AxisAlignedBB(
                    this.x,
                    this.y,
                    this.z + 1 - f,
                    this.x + 1,
                    this.y + 1,
                    this.z + 1
            );
        } else if (this.meta == 3) {
            return new AxisAlignedBB(
                    this.x,
                    this.y,
                    this.z,
                    this.x + 1,
                    this.y + 1,
                    this.z + f
            );
        } else if (this.meta == 4) {
            return new AxisAlignedBB(
                    this.x + 1 - f,
                    this.y,
                    this.z,
                    this.x + 1,
                    this.y + 1,
                    this.z + 1
            );
        } else if (this.meta == 5) {
            return new AxisAlignedBB(
                    this.x,
                    this.y,
                    this.z,
                    this.x + f,
                    this.y + 1,
                    this.z + 1
            );
        }
        return null;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        if (!target.isTransparent()) {
            if (face >= 2 && face <= 5) {
                this.meta = face;
                this.getLevel().setBlock(block, this, true, true);
                return true;
            }
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            int[] faces = {
                    0, //never use
                    1, //never use
                    3,
                    2,
                    5,
                    4
            };
            if (this.getSide(faces[this.meta]).isTransparent()) {
                this.getLevel().useBreakOn(this);
            }
        }
        return 0;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_AXE;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {this.getId(), 0, 1}
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }
}

package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.entity.Entity;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.level.Level;
import cn.nukkit.server.math.AxisAlignedBB;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.utils.BlockColor;

/**
 * Created on 2015/12/8 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
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
    public boolean canBeClimbed() {
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

        double f = 0.1875;

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
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!target.isTransparent()) {
            if (face.getIndex() >= 2 && face.getIndex() <= 5) {
                this.meta = face.getIndex();
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
            if (!this.getSide(BlockFace.fromIndex(faces[this.meta])).isSolid()) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }
}

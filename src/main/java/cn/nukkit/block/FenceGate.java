package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.level.sound.DoorSound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Doors;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class FenceGate extends Transparent {

    public FenceGate() {
        this(0);
    }

    public FenceGate(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FENCE_GATE_OAK;
    }

    @Override
    public String getName() {
        return "Oak Fence Gate";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_AXE;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        if ((this.getDamage() & 0x04) > 0) {
            return null;
        }
        int i = this.getDamage() & 0x03;
        if (i == 2 || i == 0) {
            return new AxisAlignedBB(
                    x,
                    y,
                    z + 0.375,
                    x + 1,
                    y + 1.5,
                    z + 0.625
            );
        } else {
            return new AxisAlignedBB(
                    x + 0.375,
                    y,
                    z,
                    x + 0.625,
                    y + 1.5,
                    z + 1
            );
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        int[] faces = new int[]{3, 0, 1, 2};
        this.meta = faces[(player != null) ? player.getDirection() : 0] & 0x03;
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {this.getId(), 0, 1}
        };
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player == null) return false;
        if (!Doors.toggleOpenState(this, player)) return false;
        this.getLevel().setBlock(this, this, true);
        this.getLevel().addSound(new DoorSound(this));
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }
}

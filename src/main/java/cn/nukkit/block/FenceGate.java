package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.level.sound.DoorSound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.utils.Color;

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
        return 5;
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
        if (player == null) {
            return false;
        }

        double rotation = (player.yaw - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }

        int originDirection = this.meta & 0x01;

        int direction;
        if (originDirection == 0) {
            if (rotation >= 0 && rotation < 180) {
                direction = 2;
            } else {
                direction = 0;
            }
        } else {
            if (rotation >= 90 && rotation < 270) {
                direction = 3;
            } else {
                direction = 1;
            }
        }

        this.meta = direction | ((~this.meta) & 0x04);
        this.getLevel().setBlock(this, this, true);
        this.getLevel().addSound(new DoorSound(this));
        return true;
    }

    @Override
    public Color getMapColor() {
        return Color.woodColor;
    }
}

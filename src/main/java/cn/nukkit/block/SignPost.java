package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;

/**
 * @author Nukkit Project Team
 */
public class SignPost extends Transparent {

    public SignPost() {
        super(Block.SIGN_POST, 0);
    }

    public SignPost(int meta) {
        super(Block.SIGN_POST, meta);
    }

    public double getHardness() {
        return 1;
    }

    public boolean isSolid() {
        return false;
    }

    public String getName() {
        return "Sign Post";
    }

    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        if (face != 0) {
            if (face < 2 || face > 5) {
                meta = (int) Math.floor(((player.yaw + 180) * 16 / 360) + 0.5) & 0x0f;
                getLevel().setBlock(block, Block.get(Item.SIGN_POST, meta), true);

                return true;
            } else {
                meta = face;
                getLevel().setBlock(block, Block.get(Item.WALL_SIGN, meta), true);

                return true;
            }
        }

        return false;
    }

    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (getSide(0).getId() == Block.AIR) {
                getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    public boolean onBreak(Item item) {
        getLevel().setBlock(this, new Air(), true, true);

        return true;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{{Item.SIGN, 0, 1}};
    }

    public int getToolType() {
        return Tool.TYPE_AXE;
    }

}

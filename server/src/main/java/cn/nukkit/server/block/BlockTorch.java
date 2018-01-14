package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemBlock;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.util.BlockColor;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockTorch extends BlockFlowable {

    public BlockTorch() {
        this(0);
    }

    public BlockTorch(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Torch";
    }

    @Override
    public int getId() {
        return TORCH;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public int onUpdate(int type) {
        if (type == NukkitLevel.BLOCK_UPDATE_NORMAL) {
            Block below = this.down();
            int side = this.getDamage();
            int[] faces = new int[]{
                    0, //0
                    4, //1
                    5, //2
                    2, //3
                    3, //4
                    0, //5
                    0  //6
            };

            if (this.getSide(BlockFace.fromIndex(faces[side])).isTransparent() && !(side == 0 && (below instanceof BlockFence || below.getId() == COBBLE_WALL))) {
                this.getLevel().useBreakOn(this);

                return NukkitLevel.BLOCK_UPDATE_NORMAL;
            }

        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block below = this.down();

        if (!target.isTransparent() && face != BlockFace.DOWN) {
            int[] faces = new int[]{
                    0, //0, nerver used
                    5, //1
                    4, //2
                    3, //3
                    2, //4
                    1, //5
            };
            this.meta = faces[face.getIndex()];
            this.getLevel().setBlock(block, this, true, true);

            return true;
        } else if (!below.isTransparent() || below instanceof BlockFence || below.getId() == COBBLE_WALL) {
            this.meta = 0;
            this.getLevel().setBlock(block, this, true, true);

            return true;
        }
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    public BlockFace getFacing() {
        return getFacing(this.meta);
    }

    public BlockFace getFacing(int meta) {
        switch (meta) {
            case 1:
                return BlockFace.EAST;
            case 2:
                return BlockFace.WEST;
            case 3:
                return BlockFace.SOUTH;
            case 4:
                return BlockFace.NORTH;
            case 5:
            default:
                return BlockFace.UP;
        }
    }
}

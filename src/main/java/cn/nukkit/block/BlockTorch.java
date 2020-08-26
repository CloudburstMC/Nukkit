package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockTorch extends BlockFlowable implements Faceable {

    private static final int[] faces = new int[]{
            0, //0, never used
            5, //1
            4, //2
            3, //3
            2, //4
            1, //5
    };

    private static final int[] faces2 = new int[]{
            0, //0
            4, //1
            5, //2
            2, //3
            3, //4
            0, //5
            0  //6
    };

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
        return 14;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block below = this.down();
            int side = this.getDamage();
            Block block = this.getSide(BlockFace.fromIndex(faces2[side]));
            int id = block.getId();

            if ((block.isTransparent() && !(side == 0 && (below instanceof BlockFence || below.getId() == COBBLE_WALL))) && id != GLASS && id != STAINED_GLASS) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        int side = faces[face.getIndex()];
        int bid = this.getSide(BlockFace.fromIndex(faces2[side])).getId();
        if ((!target.isTransparent() || bid == GLASS || bid == STAINED_GLASS) && face != BlockFace.DOWN) {
            this.setDamage(side);
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }

        Block below = this.down();
        if (!below.isTransparent() || below instanceof BlockFence || below.getId() == COBBLE_WALL || below.getId() == GLASS || below.getId() == STAINED_GLASS) {
            this.setDamage(0);
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

    @Override
    public BlockFace getBlockFace() {
        return getBlockFace(this.getDamage() & 0x07);
    }

    public BlockFace getBlockFace(int meta) {
        switch (meta) {
            case 1:
                return BlockFace.EAST;
            case 2:
                return BlockFace.WEST;
            case 3:
                return BlockFace.SOUTH;
            case 4:
                return BlockFace.NORTH;
            default:
                return BlockFace.UP;
        }
    }
}

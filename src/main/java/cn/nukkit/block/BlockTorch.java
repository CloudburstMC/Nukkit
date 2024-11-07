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

    private static final short[] FACES = {
            0, //0, never used
            5, //1
            4, //2
            3, //3
            2, //4
            1, //5
    };

    private static final short[] FACES_2 = {
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
            int side = this.getDamage();
            if ((side != 0 && !Block.canConnectToFullSolid(this.getSide(BlockFace.fromIndex(FACES_2[side])))) || (side == 0 && !isSupportValidBelow())) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block instanceof BlockWater || block.level.isBlockWaterloggedAt(block.getChunk(), (int) block.x, (int) block.y, (int) block.z)) {
            return false;
        }

        int side = FACES[face.getIndex()];
        if (face != BlockFace.UP) {
            if (Block.canConnectToFullSolid(this.getSide(BlockFace.fromIndex(FACES_2[side])))) {
                this.setDamage(side);
                return this.getLevel().setBlock(this, this, true, true);
            }
            return false;
        }

        if (isSupportValidBelow()) {
            this.setDamage(0);
            return this.getLevel().setBlock(this, this, true, true);
        }
        return false;
    }

    private boolean isSupportValidBelow() {
        Block block = this.down();
        if (!block.isTransparent() || block.isNarrowSurface()) {
            return true;
        }
        return Block.canStayOnFullSolid(block);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
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

    @Override
    public boolean breakWhenPushed() {
        return true;
    }
}

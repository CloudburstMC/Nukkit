package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.math.Vector3;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockRedstoneTorch extends BlockTorch {

    public BlockRedstoneTorch() {
        this(0);
    }

    public BlockRedstoneTorch(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Redstone Torch";
    }

    @Override
    public int getId() {
        return REDSTONE_TORCH;
    }

    @Override
    public int getLightLevel() {
        return 7;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block below = this.down();
        Vector3 pos = getLocation();

        if (!target.isTransparent() && face != BlockFace.DOWN) {
            this.meta = getFacing(face.getIndex()).getIndex();
            this.getLevel().setBlock(block, this, true, true);

            for (BlockFace side : BlockFace.values()) {
                this.level.updateAround(pos.getSide(side));
            }
            return true;
        } else if (!below.isTransparent() || below instanceof BlockFence || below.getId() == COBBLE_WALL) {
            this.meta = 0;
            this.getLevel().setBlock(block, this, true, true);

            for (BlockFace side : BlockFace.values()) {
                this.level.updateAroundRedstone(pos.getSide(side), null);
            }
            return true;
        }
        return false;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        //return BlockFace.getFront(this.meta).getOpposite() != side ? 15 : 0;
        return 15;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return side == BlockFace.DOWN ? this.getWeakPower(side) : 0;
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, new BlockAir(), true, true);
        Vector3 pos = getLocation();

        for (BlockFace side : BlockFace.values()) {
            this.level.updateAroundRedstone(pos.getSide(side), null);
        }
        return true;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }
}

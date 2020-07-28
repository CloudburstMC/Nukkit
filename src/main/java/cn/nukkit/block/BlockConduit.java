package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;

public class BlockConduit extends BlockTransparent {
    public BlockConduit() {
    }

    @Override
    public int getId() {
        return CONDUIT;
    }

    @Override
    public String getName() {
        return "Conduit";
    }

    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (item.getBlock() != null && item.getBlockId() == CONDUIT && target.getId() == CONDUIT) {
            return false;
        }

        this.getLevel().setBlock(this, this, true, true);
        CompoundTag nbt = new CompoundTag()
                .putString("id", BlockEntity.CONDUIT)
                .putInt("x", (int) block.x)
                .putInt("y", (int) block.y)
                .putInt("z", (int) block.z)
                .putBoolean("IsMovable", true);

        BlockEntity entity = BlockEntity.createBlockEntity(BlockEntity.CONDUIT, getLevel().getChunk((int) block.x >> 4, (int) block.z >> 4), nbt);
        if (entity != null) {
            entity.scheduleUpdate();
        }

        return true;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIAMOND_BLOCK_COLOR;
    }

    @Override
    public double getMinX() {
        return x + (5.0/16);
    }

    @Override
    public double getMinY() {
        return y + (5.0/16);
    }

    @Override
    public double getMinZ() {
        return z + (5.0/16);
    }

    @Override
    public double getMaxX() {
        return x + (11.0/16);
    }

    @Override
    public double getMaxY() {
        return y + (11.0/16);
    }

    @Override
    public double getMaxZ() {
        return z + (11.0/16);
    }
}

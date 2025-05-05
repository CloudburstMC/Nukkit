package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockConduit extends BlockTransparentMeta {

    public BlockConduit() {
        this(0);
    }

    public BlockConduit(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Conduit";
    }

    @Override
    public int getId() {
        return CONDUIT;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public double getHardness() {
        return 3;
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
    public boolean canBePushed() {
        return false;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.FLOW_INTO_BLOCK;
    }

    @Override
    public boolean alwaysDropsOnExplosion() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (this.getLevel().setBlock(this, this, true, true)) {
            BlockEntity.createBlockEntity(BlockEntity.CONDUIT, this.getChunk(), BlockEntity.getDefaultCompound(this, BlockEntity.CONDUIT));
            return true;
        }
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIAMOND_BLOCK_COLOR;
    }
}

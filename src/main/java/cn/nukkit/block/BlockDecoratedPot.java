package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

public class BlockDecoratedPot extends BlockTransparentMeta {

    public BlockDecoratedPot() {
        this(0);
    }

    public BlockDecoratedPot(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Decorated Pot";
    }

    @Override
    public int getId() {
        return DECORATED_POT;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }

    @Override
    public double getMinX() {
        return this.x + 0.05;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.05;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.95;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.95;
    }

    private static final short[] FACES = {2, 3, 0, 1};

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setDamage(FACES[player != null ? player.getDirection().getHorizontalIndex() : 0]);
        if (this.getLevel().setBlock(this, this, true, true)) {
            BlockEntity.createBlockEntity(BlockEntity.BEACON, this.getChunk(), BlockEntity.getDefaultCompound(this, BlockEntity.DECORATED_POT));
            return true;
        }
        return false;
    }
}

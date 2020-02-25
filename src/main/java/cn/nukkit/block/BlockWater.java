package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.FLOWING_WATER;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockWater extends BlockLiquid {

    protected BlockWater(Identifier id, Identifier flowingId, Identifier stationaryId) {
        super(id, flowingId, stationaryId);
    }

    protected BlockWater(Identifier flowingId, Identifier stationaryId) {
        this(flowingId, flowingId, stationaryId);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        boolean success = target.getLevel().setBlock(block, this, true, false);
        if (success) this.getLevel().scheduleUpdate(this, this.tickRate());

        return success;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WATER_BLOCK_COLOR;
    }

    @Override
    public Block getBlock(int meta) {
        return Block.get(FLOWING_WATER, meta);
    }

    @Override
    public void onEntityCollide(Entity entity) {
        super.onEntityCollide(entity);

        if (entity.isOnFire()) {
            entity.extinguish();
        }
    }

    @Override
    public int tickRate() {
        return 5;
    }

    @Override
    public boolean usesWaterLogging() {
        return true;
    }

    public static BlockFactory factory(Identifier stationaryId) {
        return id-> new BlockWater(id, stationaryId);
    }
}

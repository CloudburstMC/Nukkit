package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Pub4Game
 * @since 2015/12/26
 */
public class BlockNetherrack extends BlockSolid {

    public BlockNetherrack() {
    }

    @Override
    public int getId() {
        return NETHERRACK;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Netherrack";
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, @Nullable Player player) {
        if (item.isNull() || !item.isFertilizer() || up().getId() != AIR) {
            return false;
        }

        IntList options = new IntArrayList(2);
        for(BlockFace face: BlockFace.Plane.HORIZONTAL) {
            int id = getSide(face).getId();
            if ((id == CRIMSON_NYLIUM || id == WARPED_NYLIUM) && !options.contains(id)) {
                options.add(id);
            }
        }
        
        int nylium;
        int size = options.size();
        if (size == 0) {
            return false;
        } else if (size == 1) {
            nylium = options.getInt(0);
        } else {
            nylium = options.getInt(ThreadLocalRandom.current().nextInt(size));
        }
        
        if (level.setBlock(this, Block.get(nylium), true)) {
            if (player == null || !player.isCreative()) {
                item.count--;
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}

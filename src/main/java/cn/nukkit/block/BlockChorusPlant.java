package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

public class BlockChorusPlant extends BlockTransparent {

    public BlockChorusPlant() {
    }

    @Override
    public int getId() {
        return CHORUS_PLANT;
    }

    @Override
    public String getName() {
        return "Chorus Plant";
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public double getResistance() {
        return 0.4;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_NONE;
    }

    private boolean isPositionValid() {
        // (a chorus plant with at least one other chorus plant horizontally adjacent) breaks unless (at least one of the vertically adjacent blocks is air)
        // (a chorus plant) breaks unless (the block below is (chorus plant or end stone)) or (any horizontally adjacent block is a (chorus plant above (chorus plant or end stone_))
        boolean horizontal = false;
        boolean horizontalSupported = false;
        Block down = down();
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            Block side = getSide(face);
            if (side.getId() == CHORUS_PLANT) {
                if (!horizontal) {
                    if (up().getId() != AIR && down.getId() != AIR) {
                        return false;
                    }
                    horizontal = true;
                }

                Block sideSupport = side.down();
                if (sideSupport.getId() == CHORUS_PLANT || sideSupport.getId() == END_STONE) {
                    horizontalSupported = true;
                }
            }
        }

        if (horizontal && horizontalSupported) {
            return true;
        }
        
        return down.getId() == CHORUS_PLANT || down.getId() == END_STONE;
    }

    @Override
    public int onUpdate(int type) {
        
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isPositionValid()) {
                level.scheduleUpdate(this, 1);
                return type;
            }
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            level.useBreakOn(this, null, null, true);
            return type;
        }
        
        return 0;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (!isPositionValid()) {
            return false;
        }
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public Item[] getDrops(Item item) {
        return ThreadLocalRandom.current().nextBoolean() ? new Item[]{Item.get(ItemID.CHORUS_FRUIT, 0, 1)} : new Item[0];
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }
}

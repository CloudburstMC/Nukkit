package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.BlockFace;

import javax.annotation.Nonnull;
import java.util.Map;

public class BlockChorusFlower extends BlockTransparent {

    public BlockChorusFlower() {
    }

    @Override
    public int getId() {
        return CHORUS_FLOWER;
    }

    @Override
    public String getName() {
        return "Chorus Flower";
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
        // Chorus flowers must be above end stone or chorus plant, or be above air and horizontally adjacent to exactly one chorus plant.
        // If these conditions are not met, the block breaks without dropping anything.
        Block down = down();
        if (down.getId() == CHORUS_PLANT || down.getId() == END_STONE) {
            return true;
        }
        if (down.getId() != AIR) {
            return false;
        }
        boolean foundPlant = false;
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            Block side = getSide(face);
            if (side.getId() == CHORUS_PLANT) {
                if (foundPlant) {
                    return false;
                }
                foundPlant = true;
            }
        }

        return foundPlant;
    }

    @Override
    public int onUpdate(int type) {

        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isPositionValid()) {
                level.scheduleUpdate(this, 1);
                return type;
            }
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            Map<Integer, Player> players = level.getChunkPlayers((int) x >> 4, (int) z >> 4);
            level.addParticle(new DestroyBlockParticle(this, this), players.values());
            level.setBlock(this, Block.get(AIR));
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
        return new Item[]{this.toItem()};
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }
}

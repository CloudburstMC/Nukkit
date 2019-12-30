package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.*;

/**
 * Created on 2016/1/5 by xtypr.
 * Package cn.nukkit.block in project nukkit .
 * The name NetherPortalBlock comes from minecraft wiki.
 */
public class BlockNetherPortal extends FloodableBlock implements Faceable {

    public BlockNetherPortal(Identifier id) {
        super(id);
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public int getLightLevel() {
        return 11;
    }

    public static void spawnPortal(Position pos)   {
        Level lvl = pos.level;
        int x = pos.getFloorX(), y = pos.getFloorY(), z = pos.getFloorZ();

        for (int xx = -1; xx < 4; xx++) {
            for (int yy = 1; yy < 4; yy++)  {
                for (int zz = -1; zz < 3; zz++) {
                    lvl.setBlockIdAt(x + xx, y + yy, z + zz, AIR);
                }
            }
        }

        lvl.setBlockIdAt(x + 1, y, z, OBSIDIAN);
        lvl.setBlockIdAt(x + 2, y, z, OBSIDIAN);

        z += 1;
        lvl.setBlockIdAt(x, y, z, OBSIDIAN);
        lvl.setBlockIdAt(x + 1, y, z, OBSIDIAN);
        lvl.setBlockIdAt(x + 2, y, z, OBSIDIAN);
        lvl.setBlockIdAt(x + 3, y, z, OBSIDIAN);

        z += 1;
        lvl.setBlockIdAt(x + 1, y, z, OBSIDIAN);
        lvl.setBlockIdAt(x + 2, y, z, OBSIDIAN);

        z -= 1;
        y += 1;
        lvl.setBlockIdAt(x, y, z, OBSIDIAN);
        lvl.setBlockIdAt(x + 1, y, z, PORTAL);
        lvl.setBlockIdAt(x + 2, y, z, PORTAL);
        lvl.setBlockIdAt(x + 3, y, z, OBSIDIAN);

        y += 1;
        lvl.setBlockIdAt(x, y, z, OBSIDIAN);
        lvl.setBlockIdAt(x + 1, y, z, PORTAL);
        lvl.setBlockIdAt(x + 2, y, z, PORTAL);
        lvl.setBlockIdAt(x + 3, y, z, OBSIDIAN);

        y += 1;
        lvl.setBlockIdAt(x, y, z, OBSIDIAN);
        lvl.setBlockIdAt(x + 1, y, z, PORTAL);
        lvl.setBlockIdAt(x + 2, y, z, PORTAL);
        lvl.setBlockIdAt(x + 3, y, z, OBSIDIAN);

        y += 1;
        lvl.setBlockIdAt(x, y, z, OBSIDIAN);
        lvl.setBlockIdAt(x + 1, y, z, OBSIDIAN);
        lvl.setBlockIdAt(x + 2, y, z, OBSIDIAN);
        lvl.setBlockIdAt(x + 3, y, z, OBSIDIAN);
    }

    @Override
    public boolean onBreak(Item item) {
        boolean result = super.onBreak(item);
        for (BlockFace face : BlockFace.values()) {
            Block b = this.getSide(face);
            if (b != null) {
                if (b instanceof BlockNetherPortal) {
                    result &= b.onBreak(item);
                }
            }
        }
        return result;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public Item toItem() {
        return Item.get(AIR, 0, 0);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x07);
    }
}

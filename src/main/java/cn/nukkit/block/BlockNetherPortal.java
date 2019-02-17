package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * Created on 2016/1/5 by xtypr.
 * Package cn.nukkit.block in project nukkit .
 * The name NetherPortalBlock comes from minecraft wiki.
 */
public class BlockNetherPortal extends BlockFlowable implements Faceable {

    public BlockNetherPortal() {
        this(0);
    }

    public BlockNetherPortal(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Nether Portal Block";
    }

    @Override
    public int getId() {
        return NETHER_PORTAL;
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

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockAir());
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

    public static void spawnPortal(Position pos)   {
        Level lvl = pos.level;
        int x = pos.getFloorX(), y = pos.getFloorY(), z = pos.getFloorZ();

        for (int xx = -1; xx < 4; xx++) {
            for (int yy = 1; yy < 4; yy++)  {
                for (int zz = -1; zz < 3; zz++) {
                    lvl.setBlockAt(x + xx, y + yy, z + zz, AIR);
                }
            }
        }

        lvl.setBlockAt(x + 1, y, z, OBSIDIAN);
        lvl.setBlockAt(x + 2, y, z, OBSIDIAN);

        z += 1;
        lvl.setBlockAt(x, y, z, OBSIDIAN);
        lvl.setBlockAt(x + 1, y, z, OBSIDIAN);
        lvl.setBlockAt(x + 2, y, z, OBSIDIAN);
        lvl.setBlockAt(x + 3, y, z, OBSIDIAN);

        z += 1;
        lvl.setBlockAt(x + 1, y, z, OBSIDIAN);
        lvl.setBlockAt(x + 2, y, z, OBSIDIAN);

        z -= 1;
        y += 1;
        lvl.setBlockAt(x, y, z, OBSIDIAN);
        lvl.setBlockAt(x + 1, y, z, NETHER_PORTAL);
        lvl.setBlockAt(x + 2, y, z, NETHER_PORTAL);
        lvl.setBlockAt(x + 3, y, z, OBSIDIAN);

        y += 1;
        lvl.setBlockAt(x, y, z, OBSIDIAN);
        lvl.setBlockAt(x + 1, y, z, NETHER_PORTAL);
        lvl.setBlockAt(x + 2, y, z, NETHER_PORTAL);
        lvl.setBlockAt(x + 3, y, z, OBSIDIAN);

        y += 1;
        lvl.setBlockAt(x, y, z, OBSIDIAN);
        lvl.setBlockAt(x + 1, y, z, NETHER_PORTAL);
        lvl.setBlockAt(x + 2, y, z, NETHER_PORTAL);
        lvl.setBlockAt(x + 3, y, z, OBSIDIAN);

        y += 1;
        lvl.setBlockAt(x, y, z, OBSIDIAN);
        lvl.setBlockAt(x + 1, y, z, OBSIDIAN);
        lvl.setBlockAt(x + 2, y, z, OBSIDIAN);
        lvl.setBlockAt(x + 3, y, z, OBSIDIAN);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x07);
    }
}

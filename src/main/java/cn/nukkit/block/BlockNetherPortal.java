package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

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

    public static void spawnPortal(Vector3f pos, Level level) {
        int x = pos.getFloorX(), y = pos.getFloorY(), z = pos.getFloorZ();

        for (int xx = -1; xx < 4; xx++) {
            for (int yy = 1; yy < 4; yy++) {
                for (int zz = -1; zz < 3; zz++) {
                    level.setBlockId(x + xx, y + yy, z + zz, AIR);
                }
            }
        }

        level.setBlockId(x + 1, y, z, OBSIDIAN);
        level.setBlockId(x + 2, y, z, OBSIDIAN);

        z += 1;
        level.setBlockId(x, y, z, OBSIDIAN);
        level.setBlockId(x + 1, y, z, OBSIDIAN);
        level.setBlockId(x + 2, y, z, OBSIDIAN);
        level.setBlockId(x + 3, y, z, OBSIDIAN);

        z += 1;
        level.setBlockId(x + 1, y, z, OBSIDIAN);
        level.setBlockId(x + 2, y, z, OBSIDIAN);

        z -= 1;
        y += 1;
        level.setBlockId(x, y, z, OBSIDIAN);
        level.setBlockId(x + 1, y, z, PORTAL);
        level.setBlockId(x + 2, y, z, PORTAL);
        level.setBlockId(x + 3, y, z, OBSIDIAN);

        y += 1;
        level.setBlockId(x, y, z, OBSIDIAN);
        level.setBlockId(x + 1, y, z, PORTAL);
        level.setBlockId(x + 2, y, z, PORTAL);
        level.setBlockId(x + 3, y, z, OBSIDIAN);

        y += 1;
        level.setBlockId(x, y, z, OBSIDIAN);
        level.setBlockId(x + 1, y, z, PORTAL);
        level.setBlockId(x + 2, y, z, PORTAL);
        level.setBlockId(x + 3, y, z, OBSIDIAN);

        y += 1;
        level.setBlockId(x, y, z, OBSIDIAN);
        level.setBlockId(x + 1, y, z, OBSIDIAN);
        level.setBlockId(x + 2, y, z, OBSIDIAN);
        level.setBlockId(x + 3, y, z, OBSIDIAN);
    }

    @Override
    public int getLightLevel() {
        return 11;
    }

    @Override
    public float getHardness() {
        return -1;
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
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x07);
    }
}

package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.event.level.NetherPortalSpawnEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Axis;
import cn.nukkit.math.Vector3;
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
        return new ItemBlock(Block.get(BlockID.AIR));
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        boolean result = super.onBreak(item);
        for (BlockFace face : BlockFace.values()) {
            Block b = this.getSide(face);
            if (b instanceof BlockNetherPortal) {
                result &= b.onBreak(item);
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

    public static boolean trySpawnPortal(Level level, Vector3 pos) {
        return trySpawnPortal(level, pos, false);
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    public static boolean trySpawnPortal(Level level, Vector3 pos, boolean force) {
        PortalBuilder builder = new PortalBuilder(level, pos, Axis.X, force);

        if (builder.isValid() && builder.portalBlockCount == 0) {
            builder.placePortalBlocks();
            return true;
        } else {
            builder = new PortalBuilder(level, pos, Axis.Z, force);

            if (builder.isValid() && builder.portalBlockCount == 0) {
                builder.placePortalBlocks();
                return true;
            } else {
                return false;
            }
        }
    }

    public static class PortalBuilder {

        private final Level level;
        private final Axis axis;
        private final BlockFace rightDir;
        private final BlockFace leftDir;
        private int portalBlockCount;
        private Vector3 bottomLeft;
        private int height;
        private int width;

        private final boolean force;

        public PortalBuilder(Level level, Vector3 pos, Axis axis, boolean force) {
            this.level = level;
            this.axis = axis;
            this.force = force;

            if (axis == Axis.X) {
                this.leftDir = BlockFace.EAST;
                this.rightDir = BlockFace.WEST;
            } else {
                this.leftDir = BlockFace.NORTH;
                this.rightDir = BlockFace.SOUTH;
            }


            for (Vector3 blockpos = pos; pos.getY() > blockpos.getY() - 21 && pos.getY() > level.getMinBlockY() && this.isEmptyBlock(getBlockId(pos.getSideVec(BlockFace.DOWN))); pos = pos.getSideVec(BlockFace.DOWN)) {
            }

            int i = this.getDistanceUntilEdge(pos, this.leftDir) - 1;

            if (i >= 0) {
                this.bottomLeft = pos.getSideVec(this.leftDir, i);
                this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);

                if (this.width < 2 || this.width > 21) {
                    this.bottomLeft = null;
                    this.width = 0;
                }
            }

            if (this.bottomLeft != null) {
                this.height = this.calculatePortalHeight();
            }
        }

        protected int getDistanceUntilEdge(Vector3 pos, BlockFace dir) {
            int i;

            for (i = 0; i < 22; ++i) {
                Vector3 v = pos.getSideVec(dir, i);

                if (!this.isEmptyBlock(getBlockId(v)) || getBlockId(v.getSideVec(BlockFace.DOWN)) != OBSIDIAN) {
                    break;
                }
            }

            return getBlockId(pos.getSideVec(dir, i)) == OBSIDIAN ? i : 0;
        }

        public int getHeight() {
            return this.height;
        }

        public int getWidth() {
            return this.width;
        }

        protected int calculatePortalHeight() {

            loop:
            for (this.height = 0; this.height < 21; ++this.height) {
                for (int i = 0; i < this.width; ++i) {
                    Vector3 blockpos = this.bottomLeft.getSideVec(this.rightDir, i).up(this.height);
                    int block = getBlockId(blockpos);

                    if (!this.isEmptyBlock(block)) {
                        break loop;
                    }

                    if (block == NETHER_PORTAL) {
                        ++this.portalBlockCount;
                    }

                    if (i == 0) {
                        block = getBlockId(blockpos.getSideVec(this.leftDir));

                        if (block != OBSIDIAN) {
                            break loop;
                        }
                    } else if (i == this.width - 1) {
                        block = getBlockId(blockpos.getSideVec(this.rightDir));

                        if (block != OBSIDIAN) {
                            break loop;
                        }
                    }
                }
            }

            for (int i = 0; i < this.width; ++i) {
                if (getBlockId(this.bottomLeft.getSideVec(this.rightDir, i).up(this.height)) != OBSIDIAN) {
                    this.height = 0;
                    break;
                }
            }

            if (this.height <= 21 && this.height >= 3) {
                return this.height;
            } else {
                this.bottomLeft = null;
                this.width = 0;
                this.height = 0;
                return 0;
            }
        }

        private int getBlockId(Vector3 pos) {
            return this.level.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
        }

        protected boolean isEmptyBlock(int id) {
            return force || id == AIR || id == FIRE || id == NETHER_PORTAL;
        }

        public boolean isValid() {
            return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
        }

        public void placePortalBlocks() {
            for (int i = 0; i < this.width; ++i) {
                Vector3 blockpos = this.bottomLeft.getSideVec(this.rightDir, i);

                for (int j = 0; j < this.height; ++j) {
                    this.level.setBlock(blockpos.up(j), Block.get(NETHER_PORTAL, this.axis == Axis.X ? 1 : this.axis == Axis.Z ? 2 : 0));
                }
            }
        }
    }

    public static Position getSafePortal(Position portal) {
        Level level = portal.getLevel();
        FullChunk chunk = portal.getChunk();
        Vector3 down = portal.getSideVec(BlockFace.DOWN);

        while (level.getBlockIdAt(chunk, down.getFloorX(), down.getFloorY(), down.getFloorZ()) == NETHER_PORTAL) {
            down = down.getSideVec(BlockFace.DOWN);
        }

        return Position.fromObject(down.up(), portal.getLevel());
    }

    public static Position findNearestPortal(Position pos) {
        Level level = pos.getLevel();
        Position found = null;
        int maxY = level.getMaxBlockY();

        for (int xx = -16; xx <= 16; xx++) {
            for (int zz = -16; zz <= 16; zz++) {
                for (int y = 0; y < maxY; y++) {
                    int x = pos.getFloorX() + xx, z = pos.getFloorZ() + zz;
                    if (level.getBlockIdAt(x, y, z) == NETHER_PORTAL) {
                        found = new Position(x, y, z, level);
                        break;
                    }
                }
            }
        }

        if (found == null) {
            return null;
        }
        Vector3 up = found.up();
        int x = up.getFloorX(), y = up.getFloorY(), z = up.getFloorZ();
        int id = level.getBlockIdAt(x, y, z);
        if (id != AIR && id != OBSIDIAN && id != NETHER_PORTAL) {
            for (int xx = -1; xx < 4; xx++) {
                for (int yy = 1; yy < 4; yy++) {
                    for (int zz = -1; zz < 3; zz++) {
                        level.setBlockAt(x + xx, y + yy, z + zz, AIR);
                    }
                }
            }
        }
        return found;
    }

    public static void spawnPortal(Position pos) {
        NetherPortalSpawnEvent ev = new NetherPortalSpawnEvent(pos);
        Server.getInstance().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return;
        }

        Level lvl = pos.level;
        int x = pos.getFloorX(), y = pos.getFloorY(), z = pos.getFloorZ();

        for (int xx = -1; xx < 4; xx++) {
            for (int yy = 1; yy < 4; yy++) {
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
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }
}

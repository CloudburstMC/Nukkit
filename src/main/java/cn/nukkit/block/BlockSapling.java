package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.level.StructureGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.ListChunkManager;
import cn.nukkit.level.generator.object.BasicGenerator;
import cn.nukkit.level.generator.object.tree.*;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Angelic47
 * Nukkit Project
 */
public class BlockSapling extends BlockFlowable {

    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;
    public static final int ACACIA = 4;
    public static final int DARK_OAK = 5;
    public static final int BIRCH_TALL = 10;

    public BlockSapling() {
        this(0);
    }

    public BlockSapling(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SAPLING;
    }

    private static final String[] NAMES = {
            "Oak Sapling",
            "Spruce Sapling",
            "Birch Sapling",
            "Jungle Sapling",
            "Acacia Sapling",
            "Dark Oak Sapling",
            "",
            ""
    };

    @Override
    public String getName() {
        return NAMES[this.getDamage() & 0x07];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!(this instanceof BlockMangrovePropagule) &&
                (block instanceof BlockWater || block.level.isBlockWaterloggedAt(block.getChunk(), (int) block.x, (int) block.y, (int) block.z))) {
            return false;
        }

        Block down = this.down();
        int id = down.getId();
        if (id == Block.GRASS || id == Block.DIRT || id == Block.FARMLAND || id == Block.PODZOL || id == MYCELIUM || id == MOSS_BLOCK || id == MUD) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }

        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public boolean onActivate(Item item, Player player) {
        if (item.getId() == Item.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
            if (player != null && !player.isCreative()) {
                item.count--;
            }

            this.level.addParticle(new BoneMealParticle(this));
            if (ThreadLocalRandom.current().nextFloat() >= 0.45) {
                return true;
            }

            return growTreeHere();
        }
        return false;
    }

    protected boolean growTreeHere() {
        BasicGenerator generator = null;
        boolean bigTree = false;

        Vector3 vector3 = new Vector3(this.x, this.y - 1, this.z);

        int woodType = this.getDamage() & 0x7;
        switch (woodType) {
            case JUNGLE:
                Vector2 vector2;
                if ((vector2 = this.findSaplings(JUNGLE)) != null) {
                    vector3 = this.add(vector2.getFloorX(), 0, vector2.getFloorY());
                    generator = new ObjectJungleBigTree(10, 20, Block.get(BlockID.WOOD, BlockWood.JUNGLE), Block.get(BlockID.LEAVES, BlockLeaves.JUNGLE));
                    bigTree = true;
                }

                if (!bigTree) {
                    generator = new NewJungleTree(4, 7);
                    vector3 = this.add(0, 0, 0);
                }
                break;
            case ACACIA:
                generator = new ObjectSavannaTree();
                vector3 = this.add(0, 0, 0);
                break;
            case DARK_OAK:
                if ((vector2 = this.findSaplings(DARK_OAK)) != null) {
                    vector3 = this.add(vector2.getFloorX(), 0, vector2.getFloorY());
                    generator = new ObjectDarkOakTree();
                    bigTree = true;
                }

                if (!bigTree) {
                    return false;
                }
                break;
            case SPRUCE:
                if ((vector2 = this.findSaplings(SPRUCE)) != null) {
                    vector3 = this.add(vector2.getFloorX(), 0, vector2.getFloorY());
                    generator = new HugeTreesGenerator(0, 0, null, null) {
                        @Override
                        public boolean generate(ChunkManager level, NukkitRandom rand, Vector3 position) {
                            ObjectBigSpruceTree object = new ObjectBigSpruceTree(0.25f, 4);
                            if (!this.ensureGrowable(level, position, object.getTreeHeight())) {
                                return false;
                            }
                            object.placeObject(level, position.getFloorX(), position.getFloorY(), position.getFloorZ(), rand);
                            return true;
                        }
                    };
                    bigTree = true;
                }

                if (bigTree) {
                    break;
                }
            default:
                ListChunkManager chunkManager = new ListChunkManager(this.level);
                ObjectTree.growTree(chunkManager, this.getFloorX(), this.getFloorY(), this.getFloorZ(), new NukkitRandom(), woodType);
                StructureGrowEvent ev = new StructureGrowEvent(this, chunkManager.getBlocks());
                this.level.getServer().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return false;
                }

                for (Block block : ev.getBlockList()) {
                    this.level.setBlock(block, block);
                }
                return true;
        }

        if (bigTree) {
            this.level.setBlock(vector3, Block.get(AIR), true, false);
            this.level.setBlock(vector3.add(1, 0, 0), Block.get(AIR), true, false);
            this.level.setBlock(vector3.add(0, 0, 1), Block.get(AIR), true, false);
            this.level.setBlock(vector3.add(1, 0, 1), Block.get(AIR), true, false);
        } else {
            this.level.setBlock(this, Block.get(AIR), true, false);
        }

        ListChunkManager chunkManager = new ListChunkManager(this.level);
        boolean success = generator.generate(chunkManager, new NukkitRandom(), vector3);
        StructureGrowEvent ev = new StructureGrowEvent(this, chunkManager.getBlocks());
        this.level.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled() || !success) {
            if (bigTree) {
                this.level.setBlock(vector3, this, true, false);
                this.level.setBlock(vector3.add(1, 0, 0), this, true, false);
                this.level.setBlock(vector3.add(0, 0, 1), this, true, false);
                this.level.setBlock(vector3.add(1, 0, 1), this, true, false);
            } else {
                this.level.setBlock(this, this, true, false);
            }
            return false;
        }

        for (Block block : ev.getBlockList()) {
            this.level.setBlock(block, block);
        }
        return true;
    }

    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().isTransparent()) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) { //Growth
            if (Utils.rand(1, 7) == 1) {
                if ((this.getDamage() & 0x08) == 0x08) {
                    growTreeHere();
                } else {
                    this.setDamage(this.getDamage() | 0x08);
                    this.getLevel().setBlock(this, this, true);
                    return Level.BLOCK_UPDATE_RANDOM;
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return 1;
    }

    public boolean isSameType(Vector3 pos, int type) {
        Block block = this.level.getBlock(pos);
        return block.getId() == this.getId() && (block.getDamage() & 0x07) == (type & 0x07);
    }

    @Override
    public Item toItem() {
        return Item.get(BlockID.SAPLING, this.getDamage() & 0x7);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }

    private static final Vector2[][] VALID_SAPLINGS = new Vector2[4][4];
    static {
        VALID_SAPLINGS[0] = new Vector2[]{new Vector2(0, 0), new Vector2(1, 0), new Vector2(0, 1), new Vector2(1, 1)};
        VALID_SAPLINGS[1] = new Vector2[]{new Vector2(0, 0), new Vector2(-1, 0), new Vector2(0, -1), new Vector2(-1, -1)};
        VALID_SAPLINGS[2] = new Vector2[]{new Vector2(0, 0), new Vector2(1, 0), new Vector2(0, -1), new Vector2(1, -1)};
        VALID_SAPLINGS[3] = new Vector2[]{new Vector2(0, 0), new Vector2(-1, 0), new Vector2(0, 1), new Vector2(-1, 1)};
    }

    private Vector2 findSaplings(int type) {
        for (Vector2[] validVectors : VALID_SAPLINGS) {
            boolean found = true;

            for (Vector2 vector2 : validVectors) {
                if (!this.isSameType(this.add(vector2.x, 0, vector2.y), type)) {
                    found = false;
                }
            }

            if (found) {
                int lowestX = 0;
                int lowestZ = 0;
                for (Vector2 vector2 : validVectors) {
                    if (vector2.getFloorX() < lowestX) {
                        lowestX = vector2.getFloorX();
                    }
                    if (vector2.getFloorY() < lowestZ) {
                        lowestZ = vector2.getFloorY();
                    }
                }
                return new Vector2(lowestX, lowestZ);
            }
        }

        return null;
    }
}

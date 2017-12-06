package cn.nukkit.server.block;

import cn.nukkit.api.event.block.BlockGrowEvent;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.Player;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemDye;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.level.Level;
import cn.nukkit.server.level.particle.BoneMealParticle;
import cn.nukkit.server.math.AxisAlignedBB;
import cn.nukkit.server.math.BlockFace;

import java.util.Random;

/**
 * Created by CreeperFace on 27. 10. 2016.
 */
public class BlockCocoa extends BlockTransparent {

    protected static final AxisAlignedBB[] EAST = new AxisAlignedBB[]{new AxisAlignedBB(0.6875D, 0.4375D, 0.375D, 0.9375D, 0.75D, 0.625D), new AxisAlignedBB(0.5625D, 0.3125D, 0.3125D, 0.9375D, 0.75D, 0.6875D), new AxisAlignedBB(0.5625D, 0.3125D, 0.3125D, 0.9375D, 0.75D, 0.6875D)};
    protected static final AxisAlignedBB[] WEST = new AxisAlignedBB[]{new AxisAlignedBB(0.0625D, 0.4375D, 0.375D, 0.3125D, 0.75D, 0.625D), new AxisAlignedBB(0.0625D, 0.3125D, 0.3125D, 0.4375D, 0.75D, 0.6875D), new AxisAlignedBB(0.0625D, 0.3125D, 0.3125D, 0.4375D, 0.75D, 0.6875D)};
    protected static final AxisAlignedBB[] NORTH = new AxisAlignedBB[]{new AxisAlignedBB(0.375D, 0.4375D, 0.0625D, 0.625D, 0.75D, 0.3125D), new AxisAlignedBB(0.3125D, 0.3125D, 0.0625D, 0.6875D, 0.75D, 0.4375D), new AxisAlignedBB(0.3125D, 0.3125D, 0.0625D, 0.6875D, 0.75D, 0.4375D)};
    protected static final AxisAlignedBB[] SOUTH = new AxisAlignedBB[]{new AxisAlignedBB(0.375D, 0.4375D, 0.6875D, 0.625D, 0.75D, 0.9375D), new AxisAlignedBB(0.3125D, 0.3125D, 0.5625D, 0.6875D, 0.75D, 0.9375D), new AxisAlignedBB(0.3125D, 0.3125D, 0.5625D, 0.6875D, 0.75D, 0.9375D)};

    public BlockCocoa() {
        this(0);
    }

    public BlockCocoa(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return COCOA;
    }

    @Override
    public String getName() {
        return "Cocoa";
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        if (boundingBox == null) {
            this.boundingBox = recalculateBoundingBox();
        }

        return this.boundingBox;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        AxisAlignedBB[] bbs;

        if (this.meta > 11) {
            this.meta = 11;
        }

        switch (meta) {
            case 0:
            case 4:
            case 8:
                bbs = NORTH;
                break;
            case 1:
            case 5:
            case 9:
                bbs = EAST;
                break;
            case 2:
            case 6:
            case 10:
                bbs = SOUTH;
                break;
            case 3:
            case 7:
            case 11:
                bbs = WEST;
                break;
            default:
                bbs = NORTH;
                break;
        }

        return bbs[this.meta / 4].getOffsetBoundingBox(x, y, z);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        return this.place(item, block, target, face, fx, fy, fz, null);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (target.getId() == Block.WOOD && target.getDamage() == BlockWood.JUNGLE) {
            if (face != BlockFace.DOWN && face != BlockFace.UP) {
                int[] faces = new int[]{
                        0,
                        0,
                        0,
                        2,
                        3,
                        1,
                };

                this.meta = faces[face.getIndex()];
                this.level.setBlock(block, this, true, true);
                return true;
            }
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            int[] faces = new int[]{
                    3, 4, 2, 5, 3, 4, 2, 5, 3, 4, 2, 5
            };

            Block side = this.getSide(BlockFace.fromIndex(faces[this.meta]));

            if (side.getId() != Block.WOOD && side.getDamage() != BlockWood.JUNGLE) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (new Random().nextInt(2) == 1) {
                if (this.meta / 4 < 2) {
                    BlockCocoa block = (BlockCocoa) this.clone();
                    block.meta += 4;
                    BlockGrowEvent ev = new BlockGrowEvent(this, block);
                    NukkitServer.getInstance().getPluginManager().callEvent(ev);

                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(this, ev.getNewState(), true, true);
                    } else {
                        return Level.BLOCK_UPDATE_RANDOM;
                    }
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }

        return 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == Item.DYE && item.getDamage() == 0x0f) {
            Block block = this.clone();
            if (this.meta / 4 < 2) {
                block.meta += 4;
                BlockGrowEvent ev = new BlockGrowEvent(this, block);
                NukkitServer.getInstance().getPluginManager().callEvent(ev);

                if (ev.isCancelled()) {
                    return false;
                }
                this.getLevel().setBlock(this, ev.getNewState(), true, true);
            }

            this.level.addParticle(new BoneMealParticle(this.add(0.5, 0.5, 0.5)));
            item.count--;
            return true;
        }

        return false;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (this.meta >= 8) {
            return new Item[]{
                    new ItemDye(3, 3)
            };
        } else {
            return new Item[]{
                    new ItemDye(3, 1)
            };
        }
    }
}

package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.player.Player;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.BlockIds.*;
import static cn.nukkit.item.ItemIds.*;

/**
 * Created by CreeperFace on 27. 10. 2016.
 */
public class BlockCocoa extends BlockTransparent implements Faceable {
    protected static final AxisAlignedBB[] BB_NORTH = {
            new SimpleAxisAlignedBB(0.375f, 0.4375f, 0.0625f, 0.625f, 0.75f, 0.3125f),
            new SimpleAxisAlignedBB(0.3125f, 0.3125f, 0.0625f, 0.6875f, 0.75f, 0.4375f),
            new SimpleAxisAlignedBB(0.3125f, 0.3125f, 0.0625f, 0.6875f, 0.75f, 0.4375f)
    };
    protected static final AxisAlignedBB[] BB_EAST  = {
            new SimpleAxisAlignedBB(0.6875f, 0.4375f, 0.375f, 0.9375f, 0.75f, 0.625f),
            new SimpleAxisAlignedBB(0.5625f, 0.3125f, 0.3125f, 0.9375f, 0.75f, 0.6875f),
            new SimpleAxisAlignedBB(0.5625f, 0.3125f, 0.3125f, 0.9375f, 0.75f, 0.6875f)
    };
    protected static final AxisAlignedBB[] BB_SOUTH = {
            new SimpleAxisAlignedBB(0.375f, 0.4375f, 0.6875f, 0.625f, 0.75f, 0.9375f),
            new SimpleAxisAlignedBB(0.3125f, 0.3125f, 0.5625f, 0.6875f, 0.75f, 0.9375f),
            new SimpleAxisAlignedBB(0.3125f, 0.3125f, 0.5625f, 0.6875f, 0.75f, 0.9375f)
    };
    protected static final AxisAlignedBB[] BB_WEST  = {
            new SimpleAxisAlignedBB(0.0625f, 0.4375f, 0.375f, 0.3125f, 0.75f, 0.625f),
            new SimpleAxisAlignedBB(0.0625f, 0.3125f, 0.3125f, 0.4375f, 0.75f, 0.6875f),
            new SimpleAxisAlignedBB(0.0625f, 0.3125f, 0.3125f, 0.4375f, 0.75f, 0.6875f)
    };
    protected static final AxisAlignedBB[] BB_ALL   = {
            BB_NORTH[0], BB_EAST[0], BB_SOUTH[0], BB_WEST[0],
            BB_NORTH[1], BB_EAST[1], BB_SOUTH[1], BB_WEST[1],
            BB_NORTH[2], BB_EAST[2], BB_SOUTH[2], BB_WEST[2],
    };

    public static final int NORTH    = 0;
    public static final int EAST     = 1;
    public static final int SOUTH    = 2;
    public static final int WEST     = 3;
    public static final int DIR_MASK = 3;

    public static final int STAGE_1    = 0;
    public static final int STAGE_2    = 1;
    public static final int STAGE_3    = 2;
    public static final int STAGE_MASK = 12;

    public BlockCocoa(Identifier id) {
        super(id);
    }

    @Override
    public void setMeta(int meta) {
        super.setMeta(meta);
    }


    @Override
    public float getMinX() {
        return this.getX() + getRelativeBoundingBox().getMinX();
    }

    @Override
    public float getMaxX() {
        return this.getX() + getRelativeBoundingBox().getMaxX();
    }

    @Override
    public float getMinY() {
        return this.getY() + getRelativeBoundingBox().getMinY();
    }

    @Override
    public float getMaxY() {
        return this.getY() + getRelativeBoundingBox().getMaxY();
    }

    @Override
    public float getMinZ() {
        return this.getZ() + getRelativeBoundingBox().getMinZ();
    }

    @Override
    public float getMaxZ() {
        return this.getZ() + getRelativeBoundingBox().getMaxZ();
    }

    private AxisAlignedBB getRelativeBoundingBox() {
        int meta = this.getMeta();
        if (meta > 11) {
            this.setMeta(meta = 11);
        }

        return BB_ALL[meta];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (target.getId() == LOG && (target.getMeta() & 0x03) == BlockLog.JUNGLE) {
            if (face != BlockFace.DOWN && face != BlockFace.UP) {
                int[] faces = new int[]{
                        0,
                        0,
                        0,
                        2,
                        3,
                        1,
                };

                this.setMeta(faces[face.getIndex()]);
                this.level.setBlock(block.getPosition(), this, true, true);
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

            Block side = this.getSide(BlockFace.fromIndex(faces[this.getMeta()]));

            if (side.getId() != LOG && side.getMeta() != BlockLog.JUNGLE) {
                this.getLevel().useBreakOn(this.getPosition());
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(2) == 1) {
                if (this.getMeta() / 4 < 2) {
                    BlockCocoa block = (BlockCocoa) this.clone();
                    block.setMeta(block.getMeta() + 4);
                    BlockGrowEvent ev = new BlockGrowEvent(this, block);
                    Server.getInstance().getPluginManager().callEvent(ev);

                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(this.getPosition(), ev.getNewState(), true, true);
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
        if (item.getId() == DYE && item.getMeta() == 0x0f) {
            Block block = this.clone();
            if (this.getMeta() / 4 < 2) {
                block.setMeta(block.getMeta() + 4);
                BlockGrowEvent ev = new BlockGrowEvent(this, block);
                Server.getInstance().getPluginManager().callEvent(ev);

                if (ev.isCancelled()) {
                    return false;
                }
                this.getLevel().setBlock(this.getPosition(), ev.getNewState(), true, true);
                this.level.addParticle(new BoneMealParticle(this.getPosition()));

                if (player != null && (player.getGamemode() & 0x01) == 0) {
                    item.decrementCount();
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public float getHardness() {
        return 0.2f;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemIds.DYE, DyeColor.BROWN.getDyeData());
    }

    @Override
    public Item[] getDrops(Item item) {
        if (this.getMeta() >= 8) {
            return new Item[]{
                    Item.get(ItemIds.DYE, 3, 3)
            };
        } else {
            return new Item[]{
                    Item.get(ItemIds.DYE, 3, 1)
            };
        }
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x07);
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }

    @Override
    public boolean canWaterlogFlowing() {
        return true;
    }
}

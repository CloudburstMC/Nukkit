package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author CreeperFace
 * @since 27. 10. 2016
 */
public class BlockCocoa extends BlockTransparentMeta implements Faceable {

    protected static final AxisAlignedBB[] EAST = new SimpleAxisAlignedBB[]{new SimpleAxisAlignedBB(0.6875D, 0.4375D, 0.375D, 0.9375D, 0.75D, 0.625D), new SimpleAxisAlignedBB(0.5625D, 0.3125D, 0.3125D, 0.9375D, 0.75D, 0.6875D), new SimpleAxisAlignedBB(0.5625D, 0.3125D, 0.3125D, 0.9375D, 0.75D, 0.6875D)};
    protected static final AxisAlignedBB[] WEST = new SimpleAxisAlignedBB[]{new SimpleAxisAlignedBB(0.0625D, 0.4375D, 0.375D, 0.3125D, 0.75D, 0.625D), new SimpleAxisAlignedBB(0.0625D, 0.3125D, 0.3125D, 0.4375D, 0.75D, 0.6875D), new SimpleAxisAlignedBB(0.0625D, 0.3125D, 0.3125D, 0.4375D, 0.75D, 0.6875D)};
    protected static final AxisAlignedBB[] NORTH = new SimpleAxisAlignedBB[]{new SimpleAxisAlignedBB(0.375D, 0.4375D, 0.0625D, 0.625D, 0.75D, 0.3125D), new SimpleAxisAlignedBB(0.3125D, 0.3125D, 0.0625D, 0.6875D, 0.75D, 0.4375D), new SimpleAxisAlignedBB(0.3125D, 0.3125D, 0.0625D, 0.6875D, 0.75D, 0.4375D)};
    protected static final AxisAlignedBB[] SOUTH = new SimpleAxisAlignedBB[]{new SimpleAxisAlignedBB(0.375D, 0.4375D, 0.6875D, 0.625D, 0.75D, 0.9375D), new SimpleAxisAlignedBB(0.3125D, 0.3125D, 0.5625D, 0.6875D, 0.75D, 0.9375D), new SimpleAxisAlignedBB(0.3125D, 0.3125D, 0.5625D, 0.6875D, 0.75D, 0.9375D)};
    protected static final AxisAlignedBB[] ALL = new AxisAlignedBB[12];

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
    public double getMinX() {
        return this.x + getRelativeBoundingBox().getMinX();
    }

    @Override
    public double getMaxX() {
        return this.x + getRelativeBoundingBox().getMaxX();
    }

    @Override
    public double getMinY() {
        return this.y + getRelativeBoundingBox().getMinY();
    }

    @Override
    public double getMaxY() {
        return this.y + getRelativeBoundingBox().getMaxY();
    }

    @Override
    public double getMinZ() {
        return this.z + getRelativeBoundingBox().getMinZ();
    }

    @Override
    public double getMaxZ() {
        return this.z + getRelativeBoundingBox().getMaxZ();
    }

    private AxisAlignedBB getRelativeBoundingBox() {
        int damage = this.getDamage();
        if (damage > 11) {
            this.setDamage(damage = 11);
        }
        AxisAlignedBB boundingBox = ALL[damage];
        if (boundingBox != null) return boundingBox;

        AxisAlignedBB[] bbs;

        switch (getDamage()) {
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

        return ALL[damage] = bbs[this.getDamage() >> 2];
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (target.getId() == Block.WOOD && (target.getDamage() & 0x03) == BlockWood.JUNGLE) {
            if (face != BlockFace.DOWN && face != BlockFace.UP) {
                int[] faces = new int[]{
                        0,
                        0,
                        0,
                        2,
                        3,
                        1,
                };

                this.setDamage(faces[face.getIndex()]);
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

            Block side = this.getSide(BlockFace.fromIndex(faces[this.getDamage()]));

            if (side.getId() != Block.WOOD && side.getDamage() != BlockWood.JUNGLE) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(2) == 1) {
                if (this.getGrowthStage() < 2) {
                    if (!this.grow()) {
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
    public boolean onActivate(@Nonnull Item item, Player player) {
        if (item.getId() == Item.DYE && item.getDamage() == 0x0f) {
            if (this.getGrowthStage() < 2) {
                if (!this.grow()) {
                    return false;
                }
                this.level.addParticle(new BoneMealParticle(this));

                if (player != null && (player.gamemode & 0x01) == 0) {
                    item.count--;
                }
            }

            return true;
        }

        return false;
    }

    public int getGrowthStage() {
        return this.getDamage() / 4;
    }

    public boolean grow() {
        Block block = this.clone();
        block.setDamage(block.getDamage() + 4);
        BlockGrowEvent ev = new BlockGrowEvent(this, block);
        Server.getInstance().getPluginManager().callEvent(ev);
        return !ev.isCancelled() && this.getLevel().setBlock(this, ev.getNewState(), true, true);
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

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemDye(DyeColor.BROWN.getDyeData());
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                new ItemDye(3, this.getDamage() >= 8 ? 3 : 1)
        };
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x07);
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

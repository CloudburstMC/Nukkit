package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;

/**
 * @author xtypr
 * @since 2015/12/8
 */
public class BlockLadder extends BlockTransparentMeta implements Faceable {

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = CommonBlockProperties.FACING_DIRECTION_BLOCK_PROPERTIES;

    public BlockLadder() {
        this(0);
    }

    public BlockLadder(int meta) {
        super(meta);
        calculateOffsets();
    }

    @Override
    public String getName() {
        return "Ladder";
    }

    @Override
    public int getId() {
        return LADDER;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public boolean canBeClimbed() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    private double offMinX;
    private double offMinZ;
    private double offMaxX;
    private double offMaxZ;

    private void calculateOffsets() {
        double f = 0.1875;

        switch (this.getDamage()) {
            case 2:
                this.offMinX = 0;
                this.offMinZ = 1 - f;
                this.offMaxX = 1;
                this.offMaxZ = 1;
                break;
            case 3:
                this.offMinX = 0;
                this.offMinZ = 0;
                this.offMaxX = 1;
                this.offMaxZ = f;
                break;
            case 4:
                this.offMinX = 1 - f;
                this.offMinZ = 0;
                this.offMaxX = 1;
                this.offMaxZ = 1;
                break;
            case 5:
                this.offMinX = 0;
                this.offMinZ = 0;
                this.offMaxX = f;
                this.offMaxZ = 1;
                break;
            default:
                this.offMinX = 0;
                this.offMinZ = 1 ;
                this.offMaxX = 1;
                this.offMaxZ = 1;
                break;
        }
    }

    @Override
    public void setDamage(int meta) {
        super.setDamage(meta);
        calculateOffsets();
    }

    @Override
    public double getMinX() {
        return this.x + offMinX;
    }

    @Override
    public double getMinZ() {
        return this.z + offMinZ;
    }

    @Override
    public double getMaxX() {
        return this.x + offMaxX;
    }

    @Override
    public double getMaxZ() {
        return this.z + offMaxZ;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return super.recalculateBoundingBox();
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (face.getHorizontalIndex() == -1 || !isSupportValid(target, face)) {
            return false;
        }
        
        this.setDamage(face.getIndex());
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }
    
    private boolean isSupportValid(Block support, BlockFace face) {
        switch (support.getId()) {
            case GLASS:
            case GLASS_PANE:
            case STAINED_GLASS:
            case STAINED_GLASS_PANE:
            case BEACON:
                return false;
            default:
                return BlockLever.isSupportValid(support, face);
        }
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            int[] faces = {
                    0, //never use
                    1, //never use
                    3,
                    2,
                    5,
                    4
            };
            BlockFace face = BlockFace.fromIndex(faces[this.getDamage()]);
            if (!isSupportValid(this.getSide(face), face.getOpposite())) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }
    
    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(Item.LADDER, 0, 1)
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

package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.inventory.GrindstoneInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;

import static cn.nukkit.block.BlockBell.ATTACHMENT_TYPE;
import static cn.nukkit.blockproperty.CommonBlockProperties.DIRECTION;

@PowerNukkitOnly
public class BlockGrindstone extends BlockTransparentMeta implements Faceable {

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(DIRECTION, ATTACHMENT_TYPE);

    @PowerNukkitOnly
    public static final int TYPE_ATTACHMENT_STANDING = 0;
    @PowerNukkitOnly
    public static final int TYPE_ATTACHMENT_HANGING = 1;
    @PowerNukkitOnly
    public static final int TYPE_ATTACHMENT_SIDE = 2;
    @PowerNukkitOnly
    public static final int TYPE_ATTACHMENT_MULTIPLE = 3;

    @PowerNukkitOnly
    public BlockGrindstone() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockGrindstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return GRINDSTONE;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Grindstone";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockGrindstone());
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.IRON_BLOCK_COLOR;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDamage() & 0b11);
    }

    @Override
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public void setBlockFace(BlockFace face) {
        if (face.getHorizontalIndex() == -1) {
            return;
        }
        setDamage(getDamage() & (DATA_MASK ^ 0b11) | face.getHorizontalIndex());
    }

    @PowerNukkitOnly
    public int getAttachmentType() {
        return (getDamage() & 0b1100) >> 2 & 0b11;
    }

    @PowerNukkitOnly
    public void setAttachmentType(int attachmentType) {
        attachmentType = attachmentType & 0b11;
        setDamage(getDamage() & (DATA_MASK ^ 0b1100) | (attachmentType << 2));
    }

    private boolean isConnectedTo(BlockFace connectedFace, int attachmentType, BlockFace blockFace) {
        BlockFace.Axis faceAxis = connectedFace.getAxis();
        switch (attachmentType) {
            case TYPE_ATTACHMENT_STANDING:
                if (faceAxis == BlockFace.Axis.Y) {
                    return connectedFace == BlockFace.DOWN;
                } else {
                    return false;
                }
            case TYPE_ATTACHMENT_HANGING:
                return connectedFace == BlockFace.UP;
            case TYPE_ATTACHMENT_SIDE:
            case TYPE_ATTACHMENT_MULTIPLE:
                return connectedFace == blockFace.getOpposite();
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!checkSupport()) {
                this.level.useBreakOn(this, Item.get(Item.DIAMOND_PICKAXE));
            }
            return type;
        }
        return 0;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (block.getId() != AIR && block.canBeReplaced()) {
            face = BlockFace.UP;
        }
        switch (face) {
            case UP:
                setAttachmentType(TYPE_ATTACHMENT_STANDING);
                setBlockFace(player.getDirection().getOpposite());
                break;
            case DOWN:
                setAttachmentType(TYPE_ATTACHMENT_HANGING);
                setBlockFace(player.getDirection().getOpposite());
                break;
            default:
                setBlockFace(face);
                setAttachmentType(TYPE_ATTACHMENT_SIDE);
        }
        if (!checkSupport()) {
            return false;
        }
        this.level.setBlock(this, this, true, true);
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean checkSupport() {
        switch (getAttachmentType()) {
            case TYPE_ATTACHMENT_STANDING:
                if (checkSupport(down())) {
                    return true;
                }
                break;
            case TYPE_ATTACHMENT_HANGING:
                if (checkSupport(up())) {
                    return true;
                }
                break;
            case TYPE_ATTACHMENT_SIDE:
                BlockFace blockFace = getBlockFace();
                if (checkSupport(getSide(blockFace.getOpposite()))) {
                    return true;
                }
                break;
        }

        return false;
    }

    private boolean checkSupport(Block support) {
        int id = support.getId();
        return id != AIR && id != BUBBLE_COLUMN && !(support instanceof BlockLiquid);
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        int attachmentType = getAttachmentType();
        BlockFace blockFace = getBlockFace();
        boolean south = this.isConnectedTo(BlockFace.SOUTH, attachmentType, blockFace);
        boolean north = this.isConnectedTo(BlockFace.NORTH, attachmentType, blockFace);
        boolean west = this.isConnectedTo(BlockFace.WEST, attachmentType, blockFace);
        boolean east = this.isConnectedTo(BlockFace.EAST, attachmentType, blockFace);
        boolean up = this.isConnectedTo(BlockFace.UP, attachmentType, blockFace);
        boolean down = this.isConnectedTo(BlockFace.DOWN, attachmentType, blockFace);

        double pixels = (2.0/16);

        double n = north ? 0 : pixels;
        double s = south ? 1 : 1 - pixels;
        double w = west ? 0 : pixels;
        double e = east ? 1 : 1 - pixels;
        double d = down ? 0 : pixels;
        double u = up ? 1 : 1 - pixels;

        return new SimpleAxisAlignedBB(
                this.x + w,
                this.y + d,
                this.z + n,
                this.x + e,
                this.y + u,
                this.z + s
        );
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        if (player != null) {
            player.addWindow(new GrindstoneInventory(player.getUIInventory(), this), Player.GRINDSTONE_WINDOW_ID);
        }
        return true;
    }
}

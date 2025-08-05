package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.inventory.GrindstoneInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

public class BlockGrindstone extends BlockTransparentMeta implements Faceable {

    public static final int TYPE_ATTACHMENT_STANDING = 0;
    public static final int TYPE_ATTACHMENT_HANGING = 1;
    public static final int TYPE_ATTACHMENT_SIDE = 2;
    public static final int TYPE_ATTACHMENT_MULTIPLE = 3;

    public BlockGrindstone() {
        this(0);
    }

    public BlockGrindstone(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Grindstone";
    }

    @Override
    public int getId() {
        return GRINDSTONE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
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

    public void setBlockFace(BlockFace face) {
        if (face.getHorizontalIndex() == -1) {
            return;
        }
        setDamage(getDamage() & (DATA_MASK ^ 0b11) | face.getHorizontalIndex());
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(GRINDSTONE));
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (block.getId() != AIR && block.canBeReplaced()) {
            face = BlockFace.UP;
        }

        switch (face) {
            case UP:
                this.setAttachmentType(TYPE_ATTACHMENT_STANDING);
                this.setBlockFace(player.getDirection().getOpposite());
                break;
            case DOWN:
                this.setAttachmentType(TYPE_ATTACHMENT_HANGING);
                this.setBlockFace(player.getDirection().getOpposite());
                break;
            default:
                this.setBlockFace(face);
                this.setAttachmentType(TYPE_ATTACHMENT_SIDE);
        }

        if (!this.checkSupport()) {
            return false;
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    public int getAttachmentType() {
        return (getDamage() & 0b1100) >> 2 & 0b11;
    }

    public void setAttachmentType(int attachmentType) {
        attachmentType = attachmentType & 0b11;
        setDamage(getDamage() & (DATA_MASK ^ 0b1100) | (attachmentType << 2));
    }

    private boolean checkSupport() {
        switch (this.getAttachmentType()) {
            case TYPE_ATTACHMENT_STANDING:
                if (down().getId() != AIR) {
                    return true;
                }
                break;
            case TYPE_ATTACHMENT_HANGING:
                if (up().getId() != AIR) {
                    return true;
                }
                break;
            case TYPE_ATTACHMENT_SIDE:
                BlockFace blockFace = getBlockFace();
                if (getSide(blockFace.getOpposite()).getId() != AIR) {
                    return true;
                }
                break;
        }

        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            player.addWindow(new GrindstoneInventory(player.getUIInventory(), this), Player.GRINDSTONE_WINDOW_ID);
        }
        return true;
    }
}

package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityHopper;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemHopper;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static cn.nukkit.blockproperty.CommonBlockProperties.FACING_DIRECTION;
import static cn.nukkit.blockproperty.CommonBlockProperties.TOGGLE;

/**
 * @author CreeperFace
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
public class BlockHopper extends BlockTransparentMeta implements RedstoneComponent, Faceable, BlockEntityHolder<BlockEntityHopper> {
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(FACING_DIRECTION, TOGGLE);

    public BlockHopper() {
        this(0);
    }

    public BlockHopper(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return HOPPER_BLOCK;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Class<? extends BlockEntityHopper> getBlockEntityClass() {
        return BlockEntityHopper.class;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.HOPPER;
    }

    @Override
    public String getName() {
        return "Hopper Block";
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 24;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @PowerNukkitDifference(info = "Using new method for checking if powered", since = "1.4.0.0-PN")
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        BlockFace facing = face.getOpposite();

        if (facing == BlockFace.UP) {
            facing = BlockFace.DOWN;
        }
        
        setBlockFace(facing);

        if (this.level.getServer().isRedstoneEnabled()) {
            boolean powered = this.isGettingPower();

            if (powered == this.isEnabled()) {
                this.setEnabled(!powered);
            }
        }

        CompoundTag nbt = new CompoundTag().putList(new ListTag<>("Items"));
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        if (player == null) {
            return false;
        }
        
        BlockEntityHopper blockEntity = getOrCreateBlockEntity();

        return player.addWindow(blockEntity.getInventory()) != -1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntityHopper blockEntity = getBlockEntity();

        if (blockEntity != null) {
            return ContainerInventory.calculateRedstone(blockEntity.getInventory());
        }

        return super.getComparatorInputOverride();
    }

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "getBlockFace()", reason = "Duplicated")
    public BlockFace getFacing() {
        return getBlockFace();
    }

    public boolean isEnabled() {
        return !getBooleanValue(TOGGLE);
    }

    public void setEnabled(boolean enabled) {
        setBooleanValue(TOGGLE, !enabled);
    }

    @Override
    public int onUpdate(int type) {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            boolean disabled = this.level.isBlockPowered(this.getLocation());

            if (disabled == this.isEnabled()) {
                this.setEnabled(!disabled);
                this.level.setBlock(this, this, false, true);
                BlockEntityHopper be = getBlockEntity();
                if (be != null) {
                    be.setDisabled(disabled);
                    if (!disabled) {
                        be.scheduleUpdate();
                    }
                }
            }

            return type;
        }

        return 0;
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
    public Item toItem() {
        return new ItemHopper();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(FACING_DIRECTION, face);
    }

    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(FACING_DIRECTION);
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return side == BlockFace.UP;
    }
}

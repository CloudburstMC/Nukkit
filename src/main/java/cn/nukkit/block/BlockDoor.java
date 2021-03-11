package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.AxisDirection;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.blockproperty.CommonBlockProperties.OPEN;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
public abstract class BlockDoor extends BlockTransparentMeta implements RedstoneComponent, Faceable {
    private static final double THICKNESS = 3.0 /16;

    // Contains a list of positions of doors, which have been opened by hand (by a player).
    // It is used to detect on redstone update, if the door should be closed if redstone is off on the update,
    // previously the door always closed, when placing an unpowered redstone at the door, this fixes it
    // and gives the vanilla behavior; no idea how to make this better :d
    private static final List<Location> manualOverrides = new ArrayList<>();

    public static final BooleanBlockProperty UPPER_BLOCK = new BooleanBlockProperty("upper_block_bit", false);
    public static final BooleanBlockProperty DOOR_HINGE = new BooleanBlockProperty("door_hinge_bit", false);
    public static final BlockProperty<BlockFace> DOOR_DIRECTION = new ArrayBlockProperty<>("direction", false, new BlockFace[]{
            BlockFace.EAST, BlockFace.SOUTH,
            BlockFace.WEST, BlockFace.NORTH
    }).ordinal(true);

    protected static final BlockProperties PROPERTIES = new BlockProperties(DOOR_DIRECTION, OPEN, UPPER_BLOCK, DOOR_HINGE);

    @Deprecated @DeprecationDetails(reason = "Use the accessors or properties instead", since = "1.4.0.0-PN", replaceWith = "CommonBlockProperties.OPEN")
    public static final int DOOR_OPEN_BIT = PROPERTIES.getOffset(OPEN.getName());

    @Deprecated @DeprecationDetails(reason = "Use the accessors or properties instead", since = "1.4.0.0-PN", replaceWith = "UPPER_BLOCK")
    public static final int DOOR_TOP_BIT = PROPERTIES.getOffset(UPPER_BLOCK.getName());

    @Deprecated @DeprecationDetails(reason = "Use the accessors or properties instead", since = "1.4.0.0-PN", replaceWith = "DOOR_HINGE")
    public static final int DOOR_HINGE_BIT = PROPERTIES.getOffset(DOOR_HINGE.getName());

    @Deprecated @DeprecationDetails(reason = "Was removed from the game", since = "1.4.0.0-PN", replaceWith = "#isGettingPower()")
    public static final int DOOR_POWERED_BIT = PROPERTIES.getBitSize();

    protected BlockDoor(int meta) {
        super(meta);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
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

    @Deprecated @DeprecationDetails(reason = "Limited amount of state data", since = "1.4.0.0-PN", replaceWith = "getCurrentState()")
    public int getFullDamage() {
        return getSignedBigDamage();
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        BlockFace position = getBlockFace().getOpposite();
        boolean isOpen = isOpen();
        boolean isRight = isRightHinged();
        
        if (isOpen) {
            return recalculateBoundingBoxWithPos(isRight? position.rotateYCCW() : position.rotateY());
        } else {
            return recalculateBoundingBoxWithPos(position);
        }
    }

    private AxisAlignedBB recalculateBoundingBoxWithPos(BlockFace pos) {
        if (pos.getAxisDirection() == AxisDirection.NEGATIVE) {
            return new SimpleAxisAlignedBB (
                    this.x,
                    this.y,
                    this.z,
                    this.x + 1 + pos.getXOffset() - (THICKNESS * pos.getXOffset()),
                    this.y + 1,
                    this.z + 1 + pos.getZOffset() - (THICKNESS * pos.getZOffset())
            );
        } else {
            return new SimpleAxisAlignedBB (
                    this.x + pos.getXOffset() - (THICKNESS * pos.getXOffset()),
                    this.y,
                    this.z + pos.getZOffset() - (THICKNESS * pos.getZOffset()),
                    this.x + 1,
                    this.y + 1,
                    this.z + 1
            );
        }
    }

    @PowerNukkitDifference(info = "Will drop the iron door item if the support is broken", since = "1.3.1.2-PN")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            this.onNormalUpdate();
            return type;
        }
        
        if (type == Level.BLOCK_UPDATE_REDSTONE && level.getServer().isRedstoneEnabled()) {
            this.onRedstoneUpdate();
            return type;
        }

        return 0;
    }

    private void onNormalUpdate() {
        Block down = this.down();
        if (isTop()) {
            if (down.getId() != this.getId() || down.getBooleanValue(UPPER_BLOCK)) {
                level.setBlock(this, Block.get(AIR), false);
            }

            /* Doesn't work with redstone
            boolean downIsOpen = down.getBooleanValue(OPEN);
            if (downIsOpen != isOpen()) {
                setOpen(downIsOpen);
                level.setBlock(this, this, false, true);
            }*/
            return;
        }

        if (down.getId() == AIR) {
            level.useBreakOn(this, getToolType() == ItemTool.TYPE_PICKAXE? Item.get(ItemID.DIAMOND_PICKAXE) : null);
        }
    }

    @PowerNukkitDifference(info = "Checking if the door was opened/closed manually.")
    private void onRedstoneUpdate() {
        if ((this.isOpen() != this.isGettingPower()) && !this.getManualOverride()) {
            if (this.isOpen() != this.isGettingPower()) {
                level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, this.isOpen() ? 15 : 0, this.isOpen() ? 0 : 15));

                this.setOpen(null, this.isGettingPower());
            }
        } else if (this.getManualOverride() && (this.isGettingPower() == this.isOpen())) {
            this.setManualOverride(false);
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setManualOverride(boolean val) {
        Location down;
        Location up;
        if (this.isTop()) {
            down = down().getLocation();
            up = getLocation();
        } else {
            down = getLocation();
            up = up().getLocation();
        }

        if (val) {
            manualOverrides.add(up);
            manualOverrides.add(down);
        } else {
            manualOverrides.remove(up);
            manualOverrides.remove(down);
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getManualOverride() {
        Location down;
        Location up;
        if (this.isTop()) {
            down = down().getLocation();
            up = getLocation();
        } else {
            down = getLocation();
            up = up().getLocation();
        }

        return manualOverrides.contains(up) || manualOverrides.contains(down);
    }

    @Override
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isGettingPower() {
        Location down;
        Location up;
        if (this.isTop()) {
            down = down().getLocation();
            up = getLocation();
        } else {
            down = getLocation();
            up = up().getLocation();
        }

        for (BlockFace side : BlockFace.values()) {
            Block blockDown = down.getSide(side).getLevelBlock();
            Block blockUp = up.getSide(side).getLevelBlock();

            if (this.level.isSidePowered(blockDown.getLocation(), side)
                    || this.level.isSidePowered(blockUp.getLocation(), side)) {
                return true;
            }
        }

        return this.level.isBlockPowered(down) || this.level.isBlockPowered(up);
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (this.y > 254 || face != BlockFace.UP) {
            return false;
        }

        Block blockUp = this.up();
        Block blockDown = this.down();
        if (!blockUp.canBeReplaced() || !blockDown.isSolid(BlockFace.UP) && !(blockDown instanceof BlockCauldron)) {
            return false;
        }

        BlockFace direction = player.getDirection();
        setBlockFace(direction);

        Block left = this.getSide(direction.rotateYCCW());
        Block right = this.getSide(direction.rotateY());
        if (left.getId() == this.getId() || (!right.isTransparent() && left.isTransparent())) { //Door hinge
            setRightHinged(true);
        }

        setTop(false);

        level.setBlock(block, this, true, false); //Bottom

        if (blockUp instanceof BlockLiquid && ((BlockLiquid) blockUp).usesWaterLogging()) {
            level.setBlock(blockUp, 1, blockUp, true, false);
        }

        BlockDoor doorTop = (BlockDoor) clone();
        doorTop.y++;
        doorTop.setTop(true);
        level.setBlock(doorTop, doorTop, true, true); //Top
        
        level.updateAround(block);

        if (level.getServer().isRedstoneEnabled() && !this.isOpen() && this.isGettingPower()) {
            this.setOpen(null, true);
        }

        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        this.setManualOverride(false);
        if (isTop()) {
            Block down = this.down();
            if (down.getId() == this.getId() && !down.getBooleanValue(UPPER_BLOCK)) {
                level.setBlock(down, Block.get(AIR), true);
            }
        } else {
            Block up = this.up();
            if (up.getId() == this.getId() && up.getBooleanValue(UPPER_BLOCK)) {
                level.setBlock(up, Block.get(BlockID.AIR), true);
            }
        }
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true);

        return true;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        return toggle(player);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void playOpenCloseSound() {
        if (this.isTop() && down() instanceof BlockDoor) {
            if (((BlockDoor) down()).isOpen()) {
                this.playOpenSound();
            } else {
                this.playCloseSound();
            }
        } else if (up() instanceof BlockDoor) {
            if (this.isOpen()) {
                this.playOpenSound();
            } else {
                this.playCloseSound();
            }
        }
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void playOpenSound() {
        level.addSound(this, Sound.RANDOM_DOOR_OPEN);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void playCloseSound() {
        level.addSound(this, Sound.RANDOM_DOOR_CLOSE);
    }

    @PowerNukkitDifference(info = "Just call the #setOpen() method.", since = "1.4.0.0-PN")
    public boolean toggle(Player player) {
        return this.setOpen(player, !this.isOpen());
    }

    @PowerNukkitDifference(info = "Using direct values, instead of toggling (fixes a redstone bug, that the door won't open). " +
            "Also adding possibility to detect, whether a player or redstone recently opened/closed the door.", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    public boolean setOpen(Player player, boolean open) {
        if (open == this.isOpen()) {
            return false;
        }

        DoorToggleEvent event = new DoorToggleEvent(this, player);
        level.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        player = event.getPlayer();

        Block down;
        Block up;
        if (this.isTop()) {
            down = down();
            up = this;
        } else {
            down = this;
            up = up();
        }

        up.setBooleanValue(OPEN, open);
        up.level.setBlock(up, up, true, true);

        down.setBooleanValue(OPEN, open);
        down.level.setBlock(down, down, true, true);

        if (player != null) {
            this.setManualOverride(this.isGettingPower() || isOpen());
        }

        playOpenCloseSound();
        return true;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setOpen(boolean open) {
        setBooleanValue(OPEN, open);
    }

    public boolean isOpen() {
        return getBooleanValue(OPEN);
    }
    
    public boolean isTop() {
        return getBooleanValue(UPPER_BLOCK);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setTop(boolean top) {
        setBooleanValue(UPPER_BLOCK, top);
    }

    @Deprecated @DeprecationDetails(reason = "Use the properties API instead", since = "1.4.0.0-PN")
    public boolean isTop(int meta) {
        return PROPERTIES.getBooleanValue(meta, UPPER_BLOCK.getName());
    }

    public boolean isRightHinged() {
        return getBooleanValue(DOOR_HINGE);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setRightHinged(boolean rightHinged) {
        setBooleanValue(DOOR_HINGE, rightHinged);
    }

    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(DOOR_DIRECTION);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(DOOR_DIRECTION, face);
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

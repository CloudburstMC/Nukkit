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
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.AxisDirection;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static cn.nukkit.blockproperty.CommonBlockProperties.OPEN;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockDoor extends BlockTransparentMeta implements Faceable {
    private static final double THICKNESS = 3.0 /16;
    
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
    
    @Deprecated @DeprecationDetails(reason = "Was removed from the game", since = "1.4.0.0-PN", replaceWith = "Level.isBlockPowered(block.getLocation())")
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
            onNormalUpdate();
            return type;
        }
        
        if (type == Level.BLOCK_UPDATE_REDSTONE && level.getServer().isRedstoneEnabled()) {
            onRedstoneUpdate();
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

            boolean downIsOpen = down.getBooleanValue(OPEN);
            if (downIsOpen != isOpen()) {
                setOpen(downIsOpen);
                level.setBlock(this, this, false, true);
            }
            return;
        }
        
        if (down.getId() == AIR) {
            level.useBreakOn(this, getToolType() == ItemTool.TYPE_PICKAXE? Item.get(ItemID.DIAMOND_PICKAXE) : null);
        }
    }

    private void onRedstoneUpdate() {
        boolean isPowered = level.isBlockPowered(this.getLocation());
        if (isOpen() != isPowered) {
            level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, isOpen() ? 15 : 0, isOpen() ? 0 : 15));

            toggle(null);
            playOpenCloseSound();
        }
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

        if (level.getServer().isRedstoneEnabled() && !isOpen() && level.isBlockPowered(getLocation())) {
            toggle(null);
        }

        return true;
    }

    @Override
    public boolean onBreak(Item item) {
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
        if (!toggle(player)) {
            return false;
        }
        
        playOpenCloseSound();
        return true;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void playOpenCloseSound() {
        if (isOpen()) {
            playOpenSound();
        } else {
            playCloseSound();
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

    public boolean toggle(Player player) {
        DoorToggleEvent event = new DoorToggleEvent(this, player);
        level.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        Block down;
        Block up;
        if (isTop()) {
            down = down();
            up = this;
        } else {
            down = this;
            up = up();
        }
        
        if( down.getId() == this.getId() && !down.getBooleanValue(UPPER_BLOCK) 
                && up.getId() == this.getId() && up.getBooleanValue(UPPER_BLOCK) ) {
            down.toggleBooleanProperty(OPEN);
            level.setBlock(down, down, true, true);
        } else {
            toggleBooleanProperty(OPEN);
            level.setBlock(this, this, true, true);
        }
        
        return true;
    }

    public boolean isOpen() {
        return getBooleanValue(OPEN);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setOpen(boolean open) {
        setBooleanValue(OPEN, open);
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

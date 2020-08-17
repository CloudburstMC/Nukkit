package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.AxisDirection;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;

import static cn.nukkit.block.BlockStairs.UPSIDE_DOWN;
import static cn.nukkit.blockproperty.CommonBlockProperties.OPEN;

/**
 * @author Pub4Game
 * @since 26.12.2015
 */
public class BlockTrapdoor extends BlockTransparentMeta implements Faceable {
    private static final double THICKNESS = 0.1875;
    
    public static final BlockProperty<BlockFace> TRAPDOOR_DIRECTION = new ArrayBlockProperty<>("direction", false, new BlockFace[] {
            // It's basically weirdo_direction but renamed
            BlockFace.EAST, BlockFace.WEST,
            BlockFace.SOUTH, BlockFace.NORTH
    }).ordinal(true);
    
    public static final BlockProperties PROPERTIES = new BlockProperties(TRAPDOOR_DIRECTION, UPSIDE_DOWN, OPEN);
    
    private static final AxisAlignedBB[] boundingBoxDamage = new AxisAlignedBB[0x1 << PROPERTIES.getBitSize()];
    
    @Deprecated @DeprecationDetails(reason = "Use the properties or the accessors", since = "1.4.0.0-PN", replaceWith = "CommonBlockProperties.OPEN")
    public static final int TRAPDOOR_OPEN_BIT = 0x08;

    @Deprecated @DeprecationDetails(reason = "Use the properties or the accessors", since = "1.4.0.0-PN", replaceWith = "BlockStairs.UPSIDE_DOWN")
    public static final int TRAPDOOR_TOP_BIT = 0x04;

    public BlockTrapdoor() {
        this(0);
    }

    public BlockTrapdoor(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return TRAPDOOR;
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
        return "Oak Trapdoor";
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    //<editor-fold desc="pre-computing the bounding boxes" defaultstate="collapsed">
    static {
        for (int damage = 0; damage < boundingBoxDamage.length; damage++) {
            AxisAlignedBB bb;
            if (PROPERTIES.getBooleanValue(damage, OPEN.getName())) {
                BlockFace face = (BlockFace) PROPERTIES.getValue(damage, TRAPDOOR_DIRECTION.getName());
                face = face.getOpposite();
                if (face.getAxisDirection() == AxisDirection.NEGATIVE) {
                    bb = new SimpleAxisAlignedBB (
                            0,
                            0,
                            0,
                            1 + face.getXOffset() - (THICKNESS * face.getXOffset()),
                            1,
                            1 + face.getZOffset() - (THICKNESS * face.getZOffset())
                    );
                } else {
                    bb = new SimpleAxisAlignedBB (
                            face.getXOffset() - (THICKNESS * face.getXOffset()),
                            0,
                            face.getZOffset() - (THICKNESS * face.getZOffset()),
                            1,
                            1,
                            1
                    );
                }
            } else if (PROPERTIES.getBooleanValue(damage, UPSIDE_DOWN.getName())) {
                bb = new SimpleAxisAlignedBB(
                        0,
                        1 - THICKNESS,
                        0,
                        1,
                        1,
                        1
                );
            } else {
                bb = new SimpleAxisAlignedBB(
                        0,
                        0,
                        0,
                        1,
                        0 + THICKNESS,
                        1
                );
            }
            
            boundingBoxDamage[damage] = bb;
        }
    }
    //</editor-fold>

    @PowerNukkitDifference(info = "The bounding box was fixed", since = "1.3.0.0-PN")
    private AxisAlignedBB getRelativeBoundingBox() {
        @SuppressWarnings("deprecation") 
        int bigDamage = getBigDamage();
        return boundingBoxDamage[bigDamage];
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

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_REDSTONE && this.level.getServer().isRedstoneEnabled()) {
            if (isOpen() != level.isBlockPowered(this.getLocation())) {
                level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, isOpen() ? 15 : 0, isOpen() ? 0 : 15));
                toggleBooleanProperty(OPEN);
                level.setBlock(this, this, true);
                playOpenCloseSound();
                return type;
            }
        }

        return 0;
    }
    
    @PowerNukkitDifference(info = "Will return false if setBlock fails and the direction is relative to where the player is facing", since = "1.4.0.0-PN")
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (face.getAxis().isHorizontal()) {
            setBlockFace(player == null? face : player.getDirection().getOpposite());
            setTop(fy > 0.5);
        } else {
            setBlockFace(player.getDirection().getOpposite());
            setTop(face != BlockFace.UP);
        }
        
        return level.setBlock(block, this, true, true);
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        if(toggle(player)) {
            playOpenCloseSound();
            return true;
        }
        return false;
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
        this.level.addSound(this, Sound.RANDOM_DOOR_OPEN);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void playCloseSound() {
        this.level.addSound(this, Sound.RANDOM_DOOR_CLOSE);
    }

    @PowerNukkitDifference(info = "Returns false if setBlock fails", since = "1.4.0.0-PN")
    public boolean toggle(Player player) {
        DoorToggleEvent ev = new DoorToggleEvent(this, player);
        getLevel().getServer().getPluginManager().callEvent(ev);
        if(ev.isCancelled()) {
            return false;
        }
        toggleBooleanProperty(OPEN);
        return getLevel().setBlock(this, this, true);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
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
        return getBooleanValue(UPSIDE_DOWN);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setTop(boolean top) {
        setBooleanValue(UPSIDE_DOWN, top);
    }

    @PowerNukkitDifference(info = "Was returning the wrong face", since = "1.3.0.0-PN")
    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(TRAPDOOR_DIRECTION);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(TRAPDOOR_DIRECTION, face);
    }
}

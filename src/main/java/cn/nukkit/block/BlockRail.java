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
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.OptionalBoolean;
import cn.nukkit.utils.Rail;
import cn.nukkit.utils.Rail.Orientation;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.nukkit.math.BlockFace.*;
import static cn.nukkit.utils.Rail.Orientation.*;

/**
 * @author Snake1999
 * @since 2016/1/11
 */
public class BlockRail extends BlockFlowable implements Faceable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty ACTIVE = new BooleanBlockProperty("rail_data_bit", false);
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperty<Rail.Orientation> UNCURVED_RAIL_DIRECTION = new ArrayBlockProperty<>("rail_direction", false, new Rail.Orientation[]{
            STRAIGHT_NORTH_SOUTH, STRAIGHT_EAST_WEST,
            ASCENDING_EAST, ASCENDING_WEST,
            ASCENDING_NORTH, ASCENDING_SOUTH
    });

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperty<Rail.Orientation> CURVED_RAIL_DIRECTION = new ArrayBlockProperty<>("rail_direction", false, new Rail.Orientation[]{
            STRAIGHT_NORTH_SOUTH, STRAIGHT_EAST_WEST,
            ASCENDING_EAST, ASCENDING_WEST,
            ASCENDING_NORTH, ASCENDING_SOUTH,
            CURVED_SOUTH_EAST, CURVED_SOUTH_WEST,
            CURVED_NORTH_WEST, CURVED_NORTH_EAST
    });

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties ACTIVABLE_PROPERTIES = new BlockProperties(UNCURVED_RAIL_DIRECTION, ACTIVE);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(CURVED_RAIL_DIRECTION);

    // 0x8: Set the block active
    // 0x7: Reset the block to normal
    // If the rail can be powered. So its a complex rail!
    protected boolean canBePowered = false;

    public BlockRail() {
        this(0);
    }

    public BlockRail(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Rail";
    }

    @Override
    public int getId() {
        return RAIL;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 0.7;
    }

    @Override
    public double getResistance() {
        return 3.5;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Optional<BlockFace> ascendingDirection = this.getOrientation().ascendingDirection();
            if (!checkCanBePlace(this.down()) || (ascendingDirection.isPresent() && !checkCanBePlace(this.getSide(ascendingDirection.get())))) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.125;
    }

    @Override
    public AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    //Information from http://minecraft.gamepedia.com/Rail
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        if (!checkCanBePlace(down)) {
            return false;
        }
        Map<BlockRail, BlockFace> railsAround = this.checkRailsAroundAffected();
        List<BlockRail> rails = new ArrayList<>(railsAround.keySet());
        List<BlockFace> faces = new ArrayList<>(railsAround.values());
        if (railsAround.size() == 1) {
            BlockRail other = rails.get(0);
            this.setRailDirection(this.connect(other, railsAround.get(other)));
        } else if (railsAround.size() == 4) {
            if (this.isAbstract()) {
                this.setRailDirection(this.connect(rails.get(faces.indexOf(SOUTH)), SOUTH, rails.get(faces.indexOf(EAST)), EAST));
            } else {
                this.setRailDirection(this.connect(rails.get(faces.indexOf(EAST)), EAST, rails.get(faces.indexOf(WEST)), WEST));
            }
        } else if (!railsAround.isEmpty()) {
            if (this.isAbstract()) {
                if (railsAround.size() == 2) {
                    BlockRail rail1 = rails.get(0);
                    BlockRail rail2 = rails.get(1);
                    this.setRailDirection(this.connect(rail1, railsAround.get(rail1), rail2, railsAround.get(rail2)));
                } else {
                    List<BlockFace> cd = Stream.of(CURVED_SOUTH_EAST, CURVED_NORTH_EAST, CURVED_SOUTH_WEST)
                            .filter(o -> faces.containsAll(o.connectingDirections()))
                            .findFirst().get().connectingDirections();
                    BlockFace f1 = cd.get(0);
                    BlockFace f2 = cd.get(1);
                    this.setRailDirection(this.connect(rails.get(faces.indexOf(f1)), f1, rails.get(faces.indexOf(f2)), f2));
                }
            } else {
                BlockFace f = faces.stream().min((f1, f2) -> (f1.getIndex() < f2.getIndex()) ? 1 : ((x == y) ? 0 : -1)).get();
                BlockFace fo = f.getOpposite();
                if (faces.contains(fo)) { //Opposite connectable
                    this.setRailDirection(this.connect(rails.get(faces.indexOf(f)), f, rails.get(faces.indexOf(fo)), fo));
                } else {
                    this.setRailDirection(this.connect(rails.get(faces.indexOf(f)), f));
                }
            }
        }
        this.level.setBlock(this, this, true, true);
        if (!isAbstract()) {
            level.scheduleUpdate(this, this, 0);
        }
        
        return true;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    private boolean checkCanBePlace(Block check) {
        if (check == null) {
            return false;
        }
        return check.isSolid(UP) || check instanceof BlockCauldron;
    }

    private Orientation connect(BlockRail rail1, BlockFace face1, BlockRail rail2, BlockFace face2) {
        this.connect(rail1, face1);
        this.connect(rail2, face2);

        if (face1.getOpposite() == face2) {
            int delta1 = (int) (this.y - rail1.y);
            int delta2 = (int) (this.y - rail2.y);

            if (delta1 == -1) {
                return Orientation.ascending(face1);
            } else if (delta2 == -1) {
                return Orientation.ascending(face2);
            }
        }
        return straightOrCurved(face1, face2);
    }

    private Orientation connect(BlockRail other, BlockFace face) {
        int delta = (int) (this.y - other.y);
        Map<BlockRail, BlockFace> rails = other.checkRailsConnected();
        if (rails.isEmpty()) { //Only one
            other.setOrientation(delta == 1 ? ascending(face.getOpposite()) : straight(face));
            return delta == -1 ? ascending(face) : straight(face);
        } else if (rails.size() == 1) { //Already connected
            BlockFace faceConnected = rails.values().iterator().next();

            if (other.isAbstract() && faceConnected != face) { //Curve!
                other.setOrientation(curved(face.getOpposite(), faceConnected));
                return delta == -1 ? ascending(face) : straight(face);
            } else if (faceConnected == face) { //Turn!
                if (!other.getOrientation().isAscending()) {
                    other.setOrientation(delta == 1 ? ascending(face.getOpposite()) : straight(face));
                }
                return delta == -1 ? ascending(face) : straight(face);
            } else if (other.getOrientation().hasConnectingDirections(NORTH, SOUTH)) { //North-south
                other.setOrientation(delta == 1 ? ascending(face.getOpposite()) : straight(face));
                return delta == -1 ? ascending(face) : straight(face);
            }
        }
        return STRAIGHT_NORTH_SOUTH;
    }

    private Map<BlockRail, BlockFace> checkRailsAroundAffected() {
        Map<BlockRail, BlockFace> railsAround = this.checkRailsAround(Arrays.asList(SOUTH, EAST, WEST, NORTH));
        return railsAround.keySet().stream()
                .filter(r -> r.checkRailsConnected().size() != 2)
                .collect(Collectors.toMap(r -> r, railsAround::get));
    }

    private Map<BlockRail, BlockFace> checkRailsAround(Collection<BlockFace> faces) {
        Map<BlockRail, BlockFace> result = new HashMap<>();
        faces.forEach(f -> {
            Block b = this.getSide(f);
            Stream.of(b, b.up(), b.down())
                    .filter(Rail::isRailBlock)
                    .forEach(block -> result.put((BlockRail) block, f));
        });
        return result;
    }

    protected Map<BlockRail, BlockFace> checkRailsConnected() {
        Map<BlockRail, BlockFace> railsAround = this.checkRailsAround(this.getOrientation().connectingDirections());
        return railsAround.keySet().stream()
                .filter(r -> r.getOrientation().hasConnectingDirections(railsAround.get(r).getOpposite()))
                .collect(Collectors.toMap(r -> r, railsAround::get));
    }

    public boolean isAbstract() {
        return this.getId() == RAIL;
    }

    public boolean canPowered() {
        return this.canBePowered;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public final Orientation getRailDirection() {
        return getOrientation();
    }

    /**
     * Changes the rail direction without changing anything else.
     * @param orientation The new orientation
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setRailDirection(Orientation orientation) {
        setPropertyValue(CURVED_RAIL_DIRECTION.getName(), orientation);
    }

    public Orientation getOrientation() {
        return (Orientation) getPropertyValue(CURVED_RAIL_DIRECTION.getName());
    }

    /**
     * Changes the rail direction and update the state in the world if the orientation changed in a single call.
     * 
     * Note that the level block won't change if the current block has already the given orientation.
     * 
     * @see #setRailDirection(Orientation) 
     * @see Level#setBlock(Vector3, int, Block, boolean, boolean) 
     */
    public void setOrientation(Orientation o) {
        if (o != getOrientation()) {
            setRailDirection(o);
            this.level.setBlock(this, this, false, true);
        }
    }

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit",
            reason = "This hack is no longer needed after the block state implementation and is no longer maintained")
    public int getRealMeta() {
        // Check if this can be powered
        // Avoid modifying the value from meta (The rail orientation may be false)
        // Reason: When the rail is curved, the meta will return STRAIGHT_NORTH_SOUTH.
        // OR Null Pointer Exception
        if (!isAbstract()) {
            return getDamage() & 0x7;
        }
        // Return the default: This meta
        return getDamage();
    }

    public boolean isActive() {
        return getProperties().contains(ACTIVE) && getBooleanValue(ACTIVE);
    }

    /**
     * Changes the active flag and update the state in the world in a single call.
     * 
     * The active flag will not change if the block state don't have the {@link #ACTIVE} property, 
     * and it will not throw exceptions related to missing block properties. 
     *
     * The level block will always update.
     *
     * @see #setRailDirection(Orientation)
     * @see Level#setBlock(Vector3, int, Block, boolean, boolean)
     */
    public void setActive(boolean active) {
        if (getProperties().contains(ACTIVE)) {
            setRailActive(active);
        }
        level.setBlock(this, this, true, true);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public OptionalBoolean isRailActive() {
        return getProperties().contains(ACTIVE)? 
                OptionalBoolean.of(getBooleanValue(ACTIVE)) : 
                OptionalBoolean.empty();
    }

    /**
     * @throws NoSuchElementException If attempt to set the rail to active but it don't have the {@link #ACTIVE} property.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setRailActive(boolean active) throws NoSuchElementException {
        if (!active && !getProperties().contains(ACTIVE)) {
            return;
        }
        setBooleanValue(ACTIVE, active);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x07);
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    public boolean canBePulled() {
        return true;
    }
}

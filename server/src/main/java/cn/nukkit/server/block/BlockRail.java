package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.math.AxisAlignedBB;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.util.BlockColor;
import cn.nukkit.server.util.Rail;
import cn.nukkit.server.util.Rail.Orientation;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.nukkit.server.math.BlockFace.*;
import static cn.nukkit.server.util.Rail.Orientation.*;

/**
 * Created by Snake1999 on 2016/1/11.
 * Package cn.nukkit.server.block in project nukkit
 */
public class BlockRail extends BlockFlowable {

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
        if (type == NukkitLevel.BLOCK_UPDATE_NORMAL) {
            Optional<BlockFace> ascendingDirection = this.getOrientation().ascendingDirection();
            if (this.down().isTransparent() || (ascendingDirection.isPresent() && this.getSide(ascendingDirection.get()).isTransparent())) {
                this.getLevel().useBreakOn(this);
                return NukkitLevel.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public AxisAlignedBB recalculateBoundingBox() {
        return new AxisAlignedBB(
                this.x,
                this.y,
                this.z,
                this.x + 1,
                this.y + 0.125D,
                this.z + 1);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    //Information from http://minecraft.gamepedia.com/Rail
    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        if (down == null || down.isTransparent()) {
            return false;
        }
        Map<BlockRail, BlockFace> railsAround = this.checkRailsAroundAffected();
        List<BlockRail> rails = new ArrayList<>(railsAround.keySet());
        List<BlockFace> faces = new ArrayList<>(railsAround.values());
        if (railsAround.size() == 1) {
            BlockRail other = rails.get(0);
            this.meta = this.connect(other, railsAround.get(other)).metadata();
        } else if (railsAround.size() == 4) {
            if (this.isAbstract()) {
                this.meta = this.connect(rails.get(faces.indexOf(SOUTH)), SOUTH, rails.get(faces.indexOf(EAST)), EAST).metadata();
            } else {
                this.meta = this.connect(rails.get(faces.indexOf(EAST)), EAST, rails.get(faces.indexOf(WEST)), WEST).metadata();
            }
        } else if (!railsAround.isEmpty()) {
            if (this.isAbstract()) {
                if (railsAround.size() == 2) {
                    BlockRail rail1 = rails.get(0);
                    BlockRail rail2 = rails.get(1);
                    this.meta = this.connect(rail1, railsAround.get(rail1), rail2, railsAround.get(rail2)).metadata();
                } else {
                    List<BlockFace> cd = Stream.of(CURVED_SOUTH_EAST, CURVED_NORTH_EAST, CURVED_SOUTH_WEST)
                            .filter(o -> o.connectingDirections().stream().allMatch(faces::contains))
                            .findFirst().get().connectingDirections();
                    BlockFace f1 = cd.get(0);
                    BlockFace f2 = cd.get(1);
                    this.meta = this.connect(rails.get(faces.indexOf(f1)), f1, rails.get(faces.indexOf(f2)), f2).metadata();
                }
            } else {
                BlockFace f = faces.stream()
                        .sorted((f1, f2) -> (f1.getIndex() < f2.getIndex()) ? 1 : ((x == y) ? 0 : -1))
                        .findFirst().get();
                BlockFace fo = f.getOpposite();
                if (faces.contains(fo)) { //Opposite connectable
                    this.meta = this.connect(rails.get(faces.indexOf(f)), f, rails.get(faces.indexOf(fo)), fo).metadata();
                } else {
                    this.meta = this.connect(rails.get(faces.indexOf(f)), f).metadata();
                }
            }
        }
        this.level.setBlock(this, this, true, true);
        if (!isAbstract()) {
            level.scheduleUpdate(this, this, 0);
        }
        return true;
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

    public Orientation getOrientation() {
        return byMetadata(this.getRealMeta());
    }

    public void setOrientation(Orientation o) {
        if (o.metadata() != this.getRealMeta()) {
            this.setDamage(o.metadata());
            this.level.setBlock(this, this, true, true);
        }
    }

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
        return (getDamage() & 0x8) != 0;
    }

    public void setActive(boolean active) {
        if (active) {
            setDamage(getDamage() | 0x8);
        } else {
            setDamage(getDamage() & 0x7);
        }
        level.setBlock(this, this, true, true);
    }
}

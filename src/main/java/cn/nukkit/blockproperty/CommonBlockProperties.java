package cn.nukkit.blockproperty;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.math.BlockFace;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public final class CommonBlockProperties {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final String LEGACY_PROPERTY_NAME = "nukkit-legacy";

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties EMPTY_PROPERTIES = new BlockProperties();
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties LEGACY_PROPERTIES = new BlockProperties(new IntBlockProperty(LEGACY_PROPERTY_NAME, true, Block.DATA_MASK));
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties LEGACY_BIG_PROPERTIES = new BlockProperties(new UnsignedIntBlockProperty(LEGACY_PROPERTY_NAME, true, 0xFFFFFFFF));

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty OPEN = new BooleanBlockProperty("open_bit", false);
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty TOGGLE = new BooleanBlockProperty("toggle_bit", false);
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final IntBlockProperty REDSTONE_SIGNAL = new IntBlockProperty("redstone_signal", false, 15);
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty UPPER_BLOCK = new BooleanBlockProperty("upper_block_bit", false);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperty<BlockFace> FACING_DIRECTION = new ArrayBlockProperty<>("facing_direction", false, new BlockFace[] {
            // Index based
            BlockFace.DOWN, BlockFace.UP,
            BlockFace.NORTH, BlockFace.SOUTH,
            BlockFace.WEST, BlockFace.EAST,
    }).ordinal(true);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperty<BlockFace> DIRECTION = new ArrayBlockProperty<>("direction", false, new BlockFace[]{
            // Horizontal-index based
            BlockFace.SOUTH, BlockFace.WEST,
            BlockFace.NORTH, BlockFace.EAST,
    }).ordinal(true);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperty<BlockFace.Axis> PILLAR_AXIS = new ArrayBlockProperty<>("pillar_axis", false, new BlockFace.Axis[]{
            BlockFace.Axis.Y, BlockFace.Axis.X, BlockFace.Axis.Z
    });

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final IntBlockProperty DEPRECATED = new IntBlockProperty("deprecated", false, 3);

    private CommonBlockProperties() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty POWERED = new BooleanBlockProperty("powered_bit", false);
}

package cn.nukkit.blockproperty;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.value.ChiselType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.DyeColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public final class CommonBlockProperties {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties EMPTY_PROPERTIES = new BlockProperties();

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
    @Since("1.5.0.0-PN")
    public static final BooleanBlockProperty PERMANENTLY_DEAD = new BooleanBlockProperty("dead_bit", true);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties REDSTONE_SIGNAL_BLOCK_PROPERTY = new BlockProperties(REDSTONE_SIGNAL);

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
    @Since("1.5.0.0-PN")
    public static final ArrayBlockProperty<ChiselType> CHISEL_TYPE = new ArrayBlockProperty<>("chisel_type", true, ChiselType.class);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final IntBlockProperty AGE_15 = new IntBlockProperty("age", false, 15);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties FACING_DIRECTION_BLOCK_PROPERTIES = new BlockProperties(FACING_DIRECTION);

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

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperty<DyeColor> COLOR = new ArrayBlockProperty<>("color", true, new DyeColor[] {
            DyeColor.WHITE, DyeColor.ORANGE, DyeColor.MAGENTA, DyeColor.LIGHT_BLUE, DyeColor.YELLOW, DyeColor.LIME, DyeColor.PINK,
            DyeColor.GRAY, DyeColor.LIGHT_GRAY, DyeColor.CYAN, DyeColor.PURPLE, DyeColor.BLUE, DyeColor.BROWN,
            DyeColor.GREEN, DyeColor.RED, DyeColor.BLACK
    }, 4, "color", false, new String[] {
            "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue",
            "brown", "green", "red", "black"
    });

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties COLOR_BLOCK_PROPERTIES = new BlockProperties(COLOR);

    private CommonBlockProperties() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty POWERED = new BooleanBlockProperty("powered_bit", false);
}

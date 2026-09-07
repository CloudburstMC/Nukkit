package cn.nukkit.block.properties;

import cn.nukkit.block.custom.properties.BlockProperty;
import cn.nukkit.block.custom.properties.BooleanBlockProperty;
import cn.nukkit.block.custom.properties.EnumBlockProperty;
import cn.nukkit.math.BlockFace;

public class VanillaProperties {

    public static final BooleanBlockProperty UPPER_BLOCK = new BooleanBlockProperty("upper_block_bit", false);

    public static final BlockProperty<BlockFace> DIRECTION = new EnumBlockProperty<>("direction", false,
            new BlockFace[]{ BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST }).ordinal(true);

    public static final BlockProperty<BlockFace> FACING_DIRECTION = new EnumBlockProperty<>("facing_direction", false,
            new BlockFace[] { BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST }).ordinal(true);

    public static final BlockProperty<BlockFace> STAIRS_DIRECTION = new EnumBlockProperty<>("weirdo_direction", false,
            new BlockFace[]{ BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH }).ordinal(true);

    public static final BlockProperty<WallConnectionType> WALL_CONNECTION_TYPE_EAST = new EnumBlockProperty<>(
            "wall_connection_type_east", false, WallConnectionType.values());

    public static final BlockProperty<WallConnectionType> WALL_CONNECTION_TYPE_NORTH = new EnumBlockProperty<>(
            "wall_connection_type_north", false, WallConnectionType.values());

    public static final BlockProperty<WallConnectionType> WALL_CONNECTION_TYPE_SOUTH = new EnumBlockProperty<>(
            "wall_connection_type_south", false, WallConnectionType.values());

    public static final BlockProperty<WallConnectionType> WALL_CONNECTION_TYPE_WEST = new EnumBlockProperty<>(
            "wall_connection_type_west", false, WallConnectionType.values());

    public static final BooleanBlockProperty WALL_POST = new BooleanBlockProperty("wall_post_bit", false);
}

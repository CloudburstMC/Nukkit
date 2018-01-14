package cn.nukkit.server.util;

import cn.nukkit.server.api.API;
import cn.nukkit.server.block.Block;
import cn.nukkit.server.math.BlockFace;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static cn.nukkit.server.math.BlockFace.*;
import static cn.nukkit.server.util.Rail.Orientation.State.*;

/**
 * INTERNAL helper class of railway
 * <p>
 * By lmlstarqaq http://snake1999.com/
 * Creation time: 2017/7/1 17:42.
 */
@API(usage = API.Usage.BLEEDING, definition = API.Definition.INTERNAL)
public final class Rail {

    private Rail() {
        //no instance
    }

    public static boolean isRailBlock(Block block) {
        Objects.requireNonNull(block, "Rail block predicate can not accept null block");
        return isRailBlock(block.getId());
    }

    public static boolean isRailBlock(int blockId) {
        switch (blockId) {
            case Block.RAIL:
            case Block.POWERED_RAIL:
            case Block.ACTIVATOR_RAIL:
            case Block.DETECTOR_RAIL:
                return true;
            default:
                return false;
        }
    }

    public enum Orientation {
        STRAIGHT_NORTH_SOUTH(0, STRAIGHT, NORTH, SOUTH, null),
        STRAIGHT_EAST_WEST(1, STRAIGHT, EAST, WEST, null),
        ASCENDING_EAST(2, ASCENDING, EAST, WEST, EAST),
        ASCENDING_WEST(3, ASCENDING, EAST, WEST, WEST),
        ASCENDING_NORTH(4, ASCENDING, NORTH, SOUTH, NORTH),
        ASCENDING_SOUTH(5, ASCENDING, NORTH, SOUTH, SOUTH),
        CURVED_SOUTH_EAST(6, CURVED, SOUTH, EAST, null),
        CURVED_SOUTH_WEST(7, CURVED, SOUTH, WEST, null),
        CURVED_NORTH_WEST(8, CURVED, NORTH, WEST, null),
        CURVED_NORTH_EAST(9, CURVED, NORTH, EAST, null);

        private static final Orientation[] META_LOOKUP = new Orientation[values().length];

        static {
            for (Orientation o : values()) {
                META_LOOKUP[o.meta] = o;
            }
        }

        private final int meta;
        private final State state;
        private final List<BlockFace> connectingDirections;
        private final BlockFace ascendingDirection;

        Orientation(int meta, State state, BlockFace from, BlockFace to, BlockFace ascendingDirection) {
            this.meta = meta;
            this.state = state;
            this.connectingDirections = Arrays.asList(from, to);
            this.ascendingDirection = ascendingDirection;
        }

        public static Orientation byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public static Orientation straight(BlockFace face) {
            switch (face) {
                case NORTH:
                case SOUTH:
                    return STRAIGHT_NORTH_SOUTH;
                case EAST:
                case WEST:
                    return STRAIGHT_EAST_WEST;
            }
            return STRAIGHT_NORTH_SOUTH;
        }

        public static Orientation ascending(BlockFace face) {
            switch (face) {
                case NORTH:
                    return ASCENDING_NORTH;
                case SOUTH:
                    return ASCENDING_SOUTH;
                case EAST:
                    return ASCENDING_EAST;
                case WEST:
                    return ASCENDING_WEST;
            }
            return ASCENDING_EAST;
        }

        public static Orientation curved(BlockFace f1, BlockFace f2) {
            for (Orientation o : new Orientation[]{CURVED_SOUTH_EAST, CURVED_SOUTH_WEST, CURVED_NORTH_WEST, CURVED_NORTH_EAST}) {
                if (o.connectingDirections.contains(f1) && o.connectingDirections.contains(f2)) {
                    return o;
                }
            }
            return CURVED_SOUTH_EAST;
        }

        public static Orientation straightOrCurved(BlockFace f1, BlockFace f2) {
            for (Orientation o : new Orientation[]{STRAIGHT_NORTH_SOUTH, STRAIGHT_EAST_WEST, CURVED_SOUTH_EAST, CURVED_SOUTH_WEST, CURVED_NORTH_WEST, CURVED_NORTH_EAST}) {
                if (o.connectingDirections.contains(f1) && o.connectingDirections.contains(f2)) {
                    return o;
                }
            }
            return STRAIGHT_NORTH_SOUTH;
        }

        public int metadata() {
            return meta;
        }

        public boolean hasConnectingDirections(BlockFace... faces) {
            return Stream.of(faces).allMatch(connectingDirections::contains);
        }

        public List<BlockFace> connectingDirections() {
            return connectingDirections;
        }

        public Optional<BlockFace> ascendingDirection() {
            return Optional.ofNullable(ascendingDirection);
        }

        public boolean isStraight() {
            return state == STRAIGHT;
        }

        public boolean isAscending() {
            return state == ASCENDING;
        }

        public boolean isCurved() {
            return state == CURVED;
        }

        public enum State {
            STRAIGHT, ASCENDING, CURVED
        }
    }
}

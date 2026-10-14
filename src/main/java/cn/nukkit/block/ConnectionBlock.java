package cn.nukkit.block;

import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.LevelException;

/**
 * A block whose four horizontal connections (fences, glass panes, iron bars) are part of the block state
 * the client receives. The client no longer works the connections out on its own, so the server has to
 * send the right state or neighbouring blocks are drawn as separate posts.
 * <p>
 * The connections are stored in the four bits of the block data above the block type bits. Their order
 * matches the order of the 16 states in the vanilla block palette: north is the lowest bit, then east,
 * south and west, so that the runtime id of a state is the runtime id of the unconnected state plus
 * the value of the connection bits.
 */
public interface ConnectionBlock {

    int CONNECTION_NORTH = 0x01;
    int CONNECTION_EAST = 0x02;
    int CONNECTION_SOUTH = 0x04;
    int CONNECTION_WEST = 0x08;
    int CONNECTION_MASK = 0x0F;

    /**
     * @return number of data bits below the connection bits that hold the block type (wood type, colour)
     */
    int getConnectionShift();

    /**
     * @return number of block types, each of them has its own set of 16 connection states in the palette
     */
    int getConnectionTypeCount();

    boolean canConnect(Block block);

    /**
     * @param face the side of this block the other block is on
     */
    default boolean canConnect(Block block, BlockFace face) {
        return this.canConnect(block);
    }

    /**
     * The four horizontal sides in the order of the connection bits.
     */
    BlockFace[] CONNECTION_FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    /**
     * The side of a stair that is a full wall, by the data of the stair.
     */
    BlockFace[] STAIRS_BACK = {BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH};

    /**
     * Fences and panes only connect to the solid back of a stair, not to the sides or the steps.
     *
     * @param face the side of the fence or pane the stair is on
     */
    static boolean connectsToStairs(Block block, BlockFace face) {
        return block instanceof BlockStairs && STAIRS_BACK[block.getDamage() & 0x03] == face.getOpposite();
    }

    /**
     * A fence gate only connects to a fence in the direction it is as wide as, not to one in front of it or behind it.
     *
     * @param face the side of the fence the gate is on
     */
    static boolean connectsToGate(Block block, BlockFace face) {
        return block instanceof BlockFenceGate && ((BlockFenceGate) block).getBlockFace().getAxis() != face.getAxis();
    }

    default int getConnections() {
        return (((Block) this).getDamage() >> this.getConnectionShift()) & CONNECTION_MASK;
    }

    default void setConnections(int connections) {
        Block block = (Block) this;
        int shift = this.getConnectionShift();
        block.setDamage((block.getDamage() & ~(CONNECTION_MASK << shift)) | ((connections & CONNECTION_MASK) << shift));
    }

    /**
     * @return the block data without the connection bits, as stored in the item
     */
    default int getTypeDamage() {
        return ((Block) this).getDamage() & ((1 << this.getConnectionShift()) - 1);
    }

    /**
     * Works the connections out from the neighbouring blocks.
     *
     * @return true if the stored connections changed
     */
    default boolean updateConnections() {
        Block block = (Block) this;
        if (block.level == null || block.level.getProvider() instanceof Anvil) {
            // The anvil format has no room for the connection bits
            return false;
        }

        int connections = 0;
        try {
            for (int i = 0; i < CONNECTION_FACES.length; i++) {
                BlockFace face = CONNECTION_FACES[i];
                if (this.canConnect(block.getSide(face), face)) {
                    connections |= 1 << i;
                }
            }
        } catch (LevelException ignore) {
            return false;
        }

        if (connections == this.getConnections()) {
            return false;
        }
        this.setConnections(connections);
        return true;
    }
}

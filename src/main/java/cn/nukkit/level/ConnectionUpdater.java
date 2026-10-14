package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.block.ConnectionBlock;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.leveldb.structure.LevelDBChunk;
import cn.nukkit.level.format.leveldb.structure.LevelDBChunkSection;

/**
 * The connections of fences, glass panes and iron bars are not saved with the chunk, because the palette used to
 * save chunks has no connection states. They are worked out again when a chunk is loaded.
 */
public final class ConnectionUpdater {

    private static final int[][] SIDES = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
    private static final int[] SIDE_BITS = {
            ConnectionBlock.CONNECTION_NORTH, ConnectionBlock.CONNECTION_EAST,
            ConnectionBlock.CONNECTION_SOUTH, ConnectionBlock.CONNECTION_WEST
    };

    private ConnectionUpdater() {
    }

    private static boolean isConnectionBlock(int id) {
        return id > 0 && id < Block.MAX_BLOCK_ID && Block.getPrototype(id, 0) instanceof ConnectionBlock;
    }

    /**
     * Works out the connections of the blocks of a chunk that has just been loaded, and of the blocks in the
     * neighbouring chunks that are next to it, which could not connect to this chunk before it was loaded.
     */
    public static void onChunkLoaded(Level level, BaseFullChunk chunk) {
        if (!(chunk instanceof LevelDBChunk) || chunk.getProvider() == null) {
            return;
        }

        process((x, z) -> level.getProvider().getLoadedChunk(x, z), chunk,
                (x, y, z, block) -> level.setBlock(x, y, z, block, true, false));
    }

    /**
     * Looks up a chunk that is already loaded, it must not load or generate one.
     */
    public interface ChunkLookup {
        BaseFullChunk getLoadedChunk(int chunkX, int chunkZ);
    }

    /**
     * Sends a block that changed in a chunk the players may already have.
     */
    public interface BlockSender {
        void send(int x, int y, int z, Block block);
    }

    public static void process(ChunkLookup lookup, BaseFullChunk chunk, BlockSender sender) {
        boolean wasChanged = chunk.hasChanged();
        boolean changed = update(lookup, chunk, null, null);

        if (changed && !wasChanged) {
            // The saved chunk does not depend on the connections, no need to write it again
            chunk.setChanged(false);
        }

        for (int[] side : SIDES) {
            BaseFullChunk neighbour = lookup.getLoadedChunk(chunk.getX() + side[0], chunk.getZ() + side[1]);
            if (neighbour instanceof LevelDBChunk) {
                update(lookup, neighbour, chunk, sender);
            }
        }
    }

    /**
     * @param chunk  the chunk whose blocks are updated
     * @param border when not null only the blocks next to this chunk are updated
     * @param sender when not null the changed blocks are sent to the players that have the chunk
     * @return true if a block changed
     */
    private static boolean update(ChunkLookup lookup, BaseFullChunk chunk, BaseFullChunk border, BlockSender sender) {
        ChunkSection[] sections = ((LevelDBChunk) chunk).getSections();
        if (sections == null) {
            return false;
        }

        boolean changed = false;
        for (ChunkSection section : sections) {
            if (!(section instanceof LevelDBChunkSection)
                    || !((LevelDBChunkSection) section).containsLegacyId(ConnectionUpdater::isConnectionBlock)) {
                continue;
            }

            int baseY = section.getY() << 4;
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    if (border != null && !isNextTo(chunk, border, x, z)) {
                        continue;
                    }
                    for (int y = 0; y < 16; y++) {
                        int fullId = section.getFullBlock(x, y, z, Block.LAYER_NORMAL);
                        int id = fullId >> Block.DATA_BITS;
                        if (!isConnectionBlock(id)) {
                            continue;
                        }

                        Block block = Block.get(id, fullId & Block.DATA_MASK);
                        ConnectionBlock connectionBlock = (ConnectionBlock) block;
                        int worldX = (chunk.getX() << 4) + x;
                        int worldY = baseY + y;
                        int worldZ = (chunk.getZ() << 4) + z;
                        int connections = getConnections(lookup, chunk, connectionBlock, worldX, worldY, worldZ);
                        if (connections == connectionBlock.getConnections()) {
                            continue;
                        }

                        connectionBlock.setConnections(connections);
                        changed = true;
                        if (sender != null) {
                            sender.send(worldX, worldY, worldZ, block);
                        } else {
                            chunk.setBlock(x, worldY, z, id, block.getDamage());
                        }
                    }
                }
            }
        }
        return changed;
    }

    private static boolean isNextTo(BaseFullChunk chunk, BaseFullChunk other, int x, int z) {
        if (other.getX() < chunk.getX()) {
            return x == 0;
        } else if (other.getX() > chunk.getX()) {
            return x == 15;
        } else if (other.getZ() < chunk.getZ()) {
            return z == 0;
        } else {
            return z == 15;
        }
    }

    private static int getConnections(ChunkLookup lookup, BaseFullChunk chunk, ConnectionBlock block, int x, int y, int z) {
        int connections = 0;
        for (int i = 0; i < SIDES.length; i++) {
            int nx = x + SIDES[i][0];
            int nz = z + SIDES[i][1];
            BaseFullChunk neighbour = (nx >> 4) == chunk.getX() && (nz >> 4) == chunk.getZ()
                    ? chunk : lookup.getLoadedChunk(nx >> 4, nz >> 4);
            if (neighbour == null) {
                continue;
            }

            int fullId = neighbour.getFullBlock(nx & 15, y, nz & 15, Block.LAYER_NORMAL);
            if (block.canConnect(Block.get(fullId >> Block.DATA_BITS, fullId & Block.DATA_MASK), ConnectionBlock.CONNECTION_FACES[i])) {
                connections |= SIDE_BITS[i];
            }
        }
        return connections;
    }
}

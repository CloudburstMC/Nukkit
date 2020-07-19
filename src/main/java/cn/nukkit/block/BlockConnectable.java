package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.BlockFace;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

@PowerNukkitOnly
@Since("1.3.0.0-PN")
public interface BlockConnectable {
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    Block getSideAtLayer(int layer, BlockFace face);
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    boolean canConnect(Block block);
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    default boolean isStraight() {
        Set<BlockFace> connections = getConnections();
        if (connections.size() != 2) {
            return false;
        }

        Iterator<BlockFace> iterator = connections.iterator();
        BlockFace a = iterator.next();
        BlockFace b = iterator.next();
        return a.getOpposite() == b;
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    default Set<BlockFace> getConnections() {
        EnumSet<BlockFace> connections = EnumSet.noneOf(BlockFace.class);
        for (BlockFace blockFace : BlockFace.Plane.HORIZONTAL) {
            if (isConnected(blockFace)) {
                connections.add(blockFace);
            }
        }
        return connections;
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    default boolean isConnected(BlockFace face) {
        return canConnect(getSideAtLayer(0, face));
    }
}

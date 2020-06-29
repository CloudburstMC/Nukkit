package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockGlassPane extends BlockThin {

    public BlockGlassPane() {
    }
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public boolean isStraight() {
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
    public Set<BlockFace> getConnections() {
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
    public boolean isConnected(BlockFace face) {
        Block side = getSideAtLayer(0, face);
        switch (side.getId()) {
            case COBBLE_WALL:
            case GLASS_PANE:
            case STAINED_GLASS_PANE:
                return true;
            default:
                return side.isSolid();
        }
    }

    @Override
    public String getName() {
        return "Glass Pane";
    }

    @Override
    public int getId() {
        return GLASS_PANE;
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}

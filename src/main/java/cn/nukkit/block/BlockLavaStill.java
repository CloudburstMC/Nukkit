package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.LAVA;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockLavaStill extends BlockLava {

    protected BlockLavaStill(Identifier id, Identifier flowingId, Identifier stationaryId) {
        super(id, flowingId, stationaryId);
    }

    protected BlockLavaStill(Identifier flowingId, Identifier stationaryId) {
        this(stationaryId, flowingId, stationaryId);
    }

    @Override
    public Block getBlock(int meta) {
        return Block.get(LAVA);
    }

    @Override
    public int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_SCHEDULED) {
            return super.onUpdate(type);
        }
        return 0;
    }

    public static BlockFactory factory(Identifier flowingId) {
        return id-> new BlockLavaStill(flowingId, id);
    }
}

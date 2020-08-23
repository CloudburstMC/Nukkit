package cn.nukkit.blockentity;

import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.positiontracking.PositionTrackingService;
import cn.nukkit.utils.MainLogger;
import it.unimi.dsi.fastutil.ints.IntList;

import java.io.IOException;

/**
 * @author joserobjr
 */
public class BlockEntityLodestone extends BlockEntitySpawnable {
    public BlockEntityLodestone(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.LODESTONE;
    }

    @Override
    public void onBreak() {
        IntList handlers;
        PositionTrackingService positionTrackingService = Server.getInstance().getPositionTrackingService();
        try {
            handlers = positionTrackingService.findTrackingHandlers(this);
            if (handlers.isEmpty()) {
                return;
            }
        } catch (IOException e) {
            MainLogger.getLogger().error("Failed to remove the tracking position handler for "+getLocation());
            return;
        }
        
        int size = handlers.size();
        for (int i = 0; i < size; i++) {
            int handler = handlers.getInt(i);
            try {
                positionTrackingService.invalidateHandler(handler);
            } catch (IOException e) {
                MainLogger.getLogger().error("Failed to remove the tracking handler "+handler+" for position "+getLocation(), e);
            }
        }
    }
}

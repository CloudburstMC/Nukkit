package cn.nukkit.blockentity;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.positiontracking.PositionTrackingService;
import cn.nukkit.utils.MainLogger;
import it.unimi.dsi.fastutil.ints.IntList;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.OptionalInt;

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockEntityLodestone extends BlockEntitySpawnable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockEntityLodestone(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public OptionalInt getTrackingHandler() {
        if (namedTag.containsInt("trackingHandler")) {
            return OptionalInt.of(namedTag.getInt("trackingHandler"));
        }
        return OptionalInt.empty();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int requestTrackingHandler() throws IOException {
        OptionalInt opt = getTrackingHandler();
        if (opt.isPresent()) {
            return opt.getAsInt();
        }

        int handler = getLevel().getServer().getPositionTrackingService().addOrReusePosition(floor());
        namedTag.putInt("trackingHandler", handler);
        return handler;
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

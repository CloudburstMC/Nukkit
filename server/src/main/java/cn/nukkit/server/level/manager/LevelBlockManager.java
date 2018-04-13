package cn.nukkit.server.level.manager;

import cn.nukkit.api.block.Block;
import cn.nukkit.server.block.behavior.BlockBehavior;
import cn.nukkit.server.block.behavior.BlockBehaviors;
import cn.nukkit.server.level.NukkitLevel;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Log4j2
public class LevelBlockManager {
    private final Queue<Vector3i> blocksToTick = new ConcurrentLinkedQueue<>();
    private final int blocksTickedPerTick;
    private final NukkitLevel level;

    public LevelBlockManager(NukkitLevel level) {
        this.level = level;
        this.blocksTickedPerTick = level.getServer().getConfiguration().getAdvanced().getBlocksTickedPerTick();
    }

    public void queueBlock(@Nonnull Block block) {
        Preconditions.checkNotNull(block, "block");
        blocksToTick.add(block.getBlockPosition());
    }

    public void dequeueBlock(@Nonnull Block block) {
        Preconditions.checkNotNull(block, "block");
        blocksToTick.remove(block.getBlockPosition());
    }

    public void onTick() {
        List<Vector3i> willTick = new ArrayList<>();
        Vector3i location;
        while (willTick.size() < blocksTickedPerTick && ((location = blocksToTick.poll()) != null)) {
            willTick.add(location);
        }

        for (Vector3i blockPos : willTick) {
            Optional<Block> blockOptional = level.getBlockIfChunkLoaded(blockPos);
            if (!blockOptional.isPresent()) {
                blocksToTick.add(blockPos);
                continue;
            }

            Block block = blockOptional.get();
            BlockBehavior behavior = BlockBehaviors.getBlockBehavior(block.getBlockState().getBlockType());
            if (!behavior.onUpdate(block)) {
                blocksToTick.add(blockPos);
            }
        }
    }
}

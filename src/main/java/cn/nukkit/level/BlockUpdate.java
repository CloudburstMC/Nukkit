package cn.nukkit.level;

import cn.nukkit.block.Block;
import com.google.common.base.Preconditions;
import com.nukkitx.math.vector.Vector3i;

import static cn.nukkit.block.BlockIds.AIR;

public class BlockUpdate {

    private final Block block;
    private final Vector3i pos;
    private final int delay;
    private final int priority;
    private final boolean checkArea;


    private BlockUpdate(Block block, Vector3i pos, int delay, int priority, boolean checkArea) {
        this.block = block;
        this.pos = pos;
        this.delay = delay;
        this.priority = priority;
        this.checkArea = checkArea;
    }

    public static BlockUpdate of(Block block, int delay) {
        return of(block, block.getPosition(), delay);
    }

    public static BlockUpdate of(Block block, Vector3i pos, int delay) {
        return of(block, pos, delay, 0);
    }

    public static BlockUpdate of(Block block, Vector3i pos, int delay, int priority) {
        return of(block, pos, delay, priority, true);
    }

    public static BlockUpdate of(Block block, Vector3i pos, int delay, int priority, boolean checkArea) {
        Preconditions.checkNotNull(block, "block");
        Preconditions.checkNotNull(pos, "pos");
        Preconditions.checkArgument(block.getId() != AIR, "Air cannot be ticked");

        return new BlockUpdate(block, pos, delay, priority, checkArea);
    }

    public Block getBlock() {
        return this.block;
    }

    public Vector3i getPos() {
        return this.pos;
    }

    public int getDelay() {
        return this.delay;
    }

    public int getPriority() {
        return this.priority;
    }

    public boolean shouldCheckArea() {
        return this.checkArea;
    }
}

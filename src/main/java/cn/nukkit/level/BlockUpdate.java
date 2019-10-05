package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.math.Vector3;
import com.google.common.base.Preconditions;

public class BlockUpdate {

    private final Block block;
    private final Vector3 pos;
    private final int delay;
    private final int priority;
    private final boolean checkArea;


    private BlockUpdate(Block block, Vector3 pos, int delay, int priority, boolean checkArea) {
        this.block = block;
        this.pos = pos;
        this.delay = delay;
        this.priority = priority;
        this.checkArea = checkArea;
    }

    public static BlockUpdate of(Block block, int delay) {
        return of(block, block, delay);
    }

    public static BlockUpdate of(Block block, Vector3 pos, int delay) {
        return of(block, pos, delay, 0);
    }

    public static BlockUpdate of(Block block, Vector3 pos, int delay, int priority) {
        return of(block, pos, delay, priority, true);
    }

    public static BlockUpdate of(Block block, Vector3 pos, int delay, int priority, boolean checkArea) {
        Preconditions.checkNotNull(block, "block");
        Preconditions.checkNotNull(pos, "pos");
        Preconditions.checkArgument(block.getId() != 0, "Air cannot be ticked");

        return new BlockUpdate(block, pos.floor(), delay, priority, checkArea);
    }

    public Block getBlock() {
        return this.block;
    }

    public Vector3 getPos() {
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

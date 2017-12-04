package cn.nukkit.server.blockentity;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.level.format.FullChunk;
import cn.nukkit.server.math.BlockVector3;
import cn.nukkit.server.nbt.tag.CompoundTag;

/**
 * Created by CreeperFace on 11.4.2017.
 */
public class BlockEntityMovingBlock extends BlockEntitySpawnable {

    public Block block;

    public BlockVector3 piston;
    public int progress;

    public boolean isMovable;

    public BlockEntityMovingBlock(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        if (nbt.contains("movingBlockData") && nbt.contains("movingBlockId")) {
            this.block = Block.get(nbt.getInt("movingBlockId"), nbt.getInt("movingBlockData"));
        } else {
            this.close();
        }

        if (nbt.contains("pistonPosX") && nbt.contains("pistonPosY") && nbt.contains("pistonPosZ")) {
            this.piston = new BlockVector3(nbt.getInt("pistonPosX"), nbt.getInt("pistonPosY"), nbt.getInt("pistonPosZ"));
        } else {
            this.close();
        }

        this.isMovable = nbt.getBoolean("isMovable");
    }

    public Block getBlock() {
        return this.block;
    }

    @Override
    public boolean isBlockEntityValid() {
        return true;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.MOVING_BLOCK)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putBoolean("isMovable", this.isMovable)
                .putFloat("movingBlockId", this.block.getId())
                .putFloat("movingBlockData", this.block.getDamage())
                .putInt("pistonPosX", this.piston.x)
                .putInt("pistonPosY", this.piston.y)
                .putInt("pistonPosZ", this.piston.z);
    }
}

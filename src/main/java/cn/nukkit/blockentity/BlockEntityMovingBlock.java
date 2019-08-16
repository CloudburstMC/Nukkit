package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by CreeperFace on 11.4.2017.
 */
public class BlockEntityMovingBlock extends BlockEntitySpawnable {

    protected String blockString;
    protected Block block;

    protected BlockVector3 piston;

    public BlockEntityMovingBlock(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (namedTag.contains("movingBlock")) {
            CompoundTag blockData = namedTag.getCompound("movingBlock");

            this.blockString = blockData.getString("name");
            this.block = Block.get(blockData.getInt("id"), blockData.getInt("meta"));
        } else {
            this.close();
        }

        if (namedTag.contains("pistonPosX") && namedTag.contains("pistonPosY") && namedTag.contains("pistonPosZ")) {
            this.piston = new BlockVector3(namedTag.getInt("pistonPosX"), namedTag.getInt("pistonPosY"), namedTag.getInt("pistonPosZ"));
        } else {
            this.piston = new BlockVector3(0, -1, 0);
        }

        super.initBlockEntity();
    }

    public CompoundTag getBlockEntity() {
        if (this.namedTag.contains("movingEntity")) {
            return this.namedTag.getCompound("movingEntity");
        }

        return null;
    }

    public Block getMovingBlock() {
        return this.block;
    }

    public String getMovingBlockString() {
        return this.blockString;
    }

    public void moveCollidedEntities(BlockEntityPistonArm piston) {

    }

    @Override
    public boolean isBlockEntityValid() {
        return this.level.getBlockIdAt(getFloorX(), getFloorY(), getFloorZ()) == BlockID.MOVING_BLOCK;
    }
}

package cn.nukkit.blockentity.impl;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.MovingBlock;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.registry.BlockRegistry;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import java.util.Set;

import static cn.nukkit.block.BlockIds.AIR;

/**
 * Created by CreeperFace on 11.4.2017.
 */
public class MovingBlockEntity extends BaseBlockEntity implements MovingBlock {

    private Block block = Block.get(AIR);
    private Block extraBlock = Block.get(AIR);
    private CompoundTag blockEntity;
    private Vector3i piston;

    public MovingBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        BlockRegistry registry = BlockRegistry.get();
        if (tag.contains("movingBlockId") && tag.contains("movingBlockData")) {
            int id = tag.getByte("movingBlockId") & 0xff;
            int meta = tag.getByte("movingBlockData");

            this.block = registry.getBlock(id, meta);
        } else {
            CompoundTag blockTag = tag.getCompound("movingBlock");
            int legacyId = registry.getLegacyId(blockTag.getString("name"));
            short meta = blockTag.getShort("val");

            this.block = registry.getBlock(legacyId, meta);

            CompoundTag extraBlockTag = tag.getCompound("movingBlockExtra");
            int extraId = registry.getLegacyId(extraBlockTag.getString("name", "minecraft:air"));
            short extraData = extraBlockTag.getShort("val");
            this.extraBlock = registry.getBlock(extraId, extraData);
            this.extraBlock.setLayer(1);
        }

        tag.listenForCompound("movingEntity", entityTag -> this.blockEntity = entityTag);

        this.piston = Vector3i.from(tag.getInt("pistonPosX"), tag.getInt("pistonPosY"), tag.getInt("pistonPosZ"));
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.tag(CompoundTag.builder()
                .stringTag("name", this.block.getId().toString())
                .shortTag("val", (short) this.block.getMeta())
                .build("movingBlock"));

        tag.tag(CompoundTag.builder()
                .stringTag("name", this.extraBlock.getId().toString())
                .shortTag("val", (short) this.extraBlock.getMeta())
                .build("movingBlockExtra"));

        tag.intTag("pistonPosX", this.piston.getX());
        tag.intTag("pistonPosY", this.piston.getY());
        tag.intTag("pistonPosZ", this.piston.getZ());

        if (this.blockEntity != null) {
            tag.tag(this.blockEntity);
        }
    }

    public Block getMovingBlock() {
        return this.block;
    }

    public void setMovingBlock(Block block) {
        this.block = block == null ? Block.get(AIR) : block;
    }

    public Block getExtraBlock() {
        return extraBlock;
    }

    public void setExtraBlock(Block extraBlock) {
        this.extraBlock = extraBlock == null ? Block.get(AIR) : extraBlock;
    }

    public CompoundTag getBlockEntity() {
        return blockEntity;
    }

    public void setBlockEntity(CompoundTag blockEntity) {
        this.blockEntity = blockEntity;
    }

    public Vector3i getPiston() {
        return piston;
    }

    public void setPiston(Vector3i piston) {
        this.piston = piston;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isSpawnable() {
        return true;
    }

    public void moveCollidedEntities(PistonBlockEntity piston, BlockFace moveDirection) {
        AxisAlignedBB bb = block.getBoundingBox();

        if (bb == null) {
            return;
        }

        float progress = piston.getProgress();
        Vector3i pos = getPosition();

        bb = bb.getOffsetBoundingBox(
                pos.getX() + (progress * moveDirection.getXOffset()) - moveDirection.getXOffset(),
                pos.getY() + (progress * moveDirection.getYOffset()) - moveDirection.getYOffset(),
                pos.getZ() + (progress * moveDirection.getZOffset()) - moveDirection.getZOffset()
        );

        Set<Entity> entities = this.getLevel().getCollidingEntities(bb);

        for (Entity entity : entities) {
            piston.moveEntity(entity, moveDirection);
        }
    }
}

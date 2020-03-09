package cn.nukkit.blockentity.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.FlowerPot;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.registry.BlockRegistry;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import static cn.nukkit.block.BlockIds.AIR;

/**
 * Created by Snake1999 on 2016/2/4.
 * Package cn.nukkit.blockentity in project Nukkit.
 */
public class FlowerPotBlockEntity extends BaseBlockEntity implements FlowerPot {

    private Block plant = Block.get(AIR);

    public FlowerPotBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        BlockRegistry registry = BlockRegistry.get();

        if (tag.contains("item") && tag.contains("mData")) {
            short id = tag.getShort("item");
            int meta = tag.getInt("mData");

            this.plant = registry.getBlock(id, meta);
        } else {
            CompoundTag plantTag = tag.getCompound("PlantBlock");
            int legacyId = registry.getLegacyId(plantTag.getString("name"));
            short meta = plantTag.getShort("val");

            this.plant = registry.getBlock(legacyId, meta);
        }
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.tag(CompoundTag.builder()
                .stringTag("name", plant.getId().toString())
                .shortTag("val", (short) plant.getMeta())
                .build("PlantBlock"));
    }

    @Override
    public boolean isValid() {
        return this.getBlock().getId() == BlockIds.FLOWER_POT;
    }

    public Block getPlant() {
        return plant.clone();
    }

    public void setPlant(Block block) {
        this.plant = block == null ? Block.get(AIR) : block;
    }

    @Override
    public boolean isSpawnable() {
        return true;
    }
}

package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * Created by Snake1999 on 2016/2/4.
 * Package cn.nukkit.blockentity in project Nukkit.
 */
public class BlockEntityFlowerPot extends BlockEntitySpawnable {
    public BlockEntityFlowerPot(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (!namedTag.contains("item")) {
            namedTag.putShort("item", 0);
        }

        if (!namedTag.contains("data")) {
            if (namedTag.contains("mData")) {
                namedTag.putInt("data", namedTag.getInt("mData"));
                namedTag.remove("mData");
            } else {
                namedTag.putInt("data", 0);
            }
        }

        super.initBlockEntity();
    }

    @Override
    public boolean isBlockEntityValid() {
        return level.getBlockIdAt(chunk, (int) x, (int) y, (int) z) == Block.FLOWER_POT_BLOCK;
    }

    private static final Int2ObjectOpenHashMap<String> HAS_STRING_ITEM_OVERRIDE = new Int2ObjectOpenHashMap<>();

    static {
        HAS_STRING_ITEM_OVERRIDE.put(BlockID.CRIMSON_ROOTS, "minecraft:crimson_roots");
        HAS_STRING_ITEM_OVERRIDE.put(BlockID.WARPED_ROOTS, "minecraft:warped_roots");
        HAS_STRING_ITEM_OVERRIDE.put(BlockID.CRIMSON_FUNGUS, "minecraft:crimson_fungus");
        HAS_STRING_ITEM_OVERRIDE.put(BlockID.WARPED_FUNGUS, "minecraft:warped_fungus");
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag tag = new CompoundTag()
                .putString("id", BlockEntity.FLOWER_POT)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        int item = namedTag.getShort("item");
        if (item != Block.AIR) {
            // Fix latest game versions not displaying legacy items correctly
            if (HAS_STRING_ITEM_OVERRIDE.containsKey(item)) {
                tag.putCompound("PlantBlock", new CompoundTag().putString("name", HAS_STRING_ITEM_OVERRIDE.get(item)));
            } else {
                tag.putShort("item", this.namedTag.getShort("item"))
                        .putInt("mData", this.namedTag.getInt("data"));
            }
        }
        return tag;
    }
}
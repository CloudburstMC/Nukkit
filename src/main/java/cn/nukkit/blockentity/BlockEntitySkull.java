package cn.nukkit.blockentity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by Snake1999 on 2016/2/3.
 * Package cn.nukkit.blockentity in project Nukkit.
 */
public class BlockEntitySkull extends BlockEntitySpawnable {
    public BlockEntitySkull(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        if (!nbt.contains("SkullType")) {
            nbt.putByte("SkullType", (byte) 0);
        }
        if (!nbt.contains("Rot")) {
            nbt.putByte("Rot", (byte) 0);
        }
        this.namedTag = nbt;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.remove("Creator");
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.SKULL)
                .put("SkullType", this.namedTag.get("SkullType"))
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .put("Rot", this.namedTag.get("Rot"));
    }

}
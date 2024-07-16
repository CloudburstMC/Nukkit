package cn.nukkit.customblock;

import cn.nukkit.customblock.container.BlockContainerFactory;
import cn.nukkit.nbt.tag.CompoundTag;
import org.cloudburstmc.nbt.NbtMap;
import lombok.Data;

@Data
public class CustomBlockState {
    private final String identifier;
    private final int legacyId;
    private final NbtMap blockState;
    private final BlockContainerFactory factory;
    private CompoundTag nukkitBlockState;

    public CompoundTag getNukkitBlockState() {
        if (this.nukkitBlockState == null) {
            this.nukkitBlockState = CustomBlockManager.convertNbtMap(this.blockState);
        }
        return this.nukkitBlockState;
    }
}
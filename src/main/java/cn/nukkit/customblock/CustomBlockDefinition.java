package cn.nukkit.customblock;

import cn.nukkit.customblock.container.BlockContainer;
import org.cloudburstmc.nbt.NbtMap;
import lombok.Data;

@Data
public class CustomBlockDefinition {
    private final String identifier;
    private final NbtMap networkData;
    private final int legacyId;
    private final Class<? extends BlockContainer> typeOf;
}

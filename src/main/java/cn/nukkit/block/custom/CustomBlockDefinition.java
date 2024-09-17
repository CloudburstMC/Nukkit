package cn.nukkit.block.custom;

import cn.nukkit.block.custom.container.BlockContainer;
import org.cloudburstmc.nbt.NbtMap;
import lombok.Data;

@Data
public class CustomBlockDefinition {
    private final String identifier;
    private final NbtMap networkData;
    private final int legacyId;
    private final Class<? extends BlockContainer> typeOf;
}

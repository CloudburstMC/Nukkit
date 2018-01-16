package cn.nukkit.server.network.minecraft.data;

import cn.nukkit.api.util.BoundingBox;
import lombok.Value;

@Value
public class StructureSettings {
    private boolean integrity;
    private int seed;
    private int mirror;
    private int rotation;
    private boolean ignoreEntities;
    private boolean ignoreStructureBlocks;
    private BoundingBox boundingBox;
}

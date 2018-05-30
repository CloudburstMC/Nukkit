package com.nukkitx.server.network.bedrock.data;

import com.nukkitx.api.util.BoundingBox;
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

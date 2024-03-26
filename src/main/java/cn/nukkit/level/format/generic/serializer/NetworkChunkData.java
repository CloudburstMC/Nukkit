package cn.nukkit.level.format.generic.serializer;

import cn.nukkit.level.DimensionData;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NetworkChunkData {

    private int chunkSections;
    private final DimensionData dimensionData;
}

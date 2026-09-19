package cn.nukkit.network.protocol.types;

import lombok.Value;

import java.util.UUID;

@Value
public class DimensionDefinition {

    String id;
    int maximumHeight;
    int minimumHeight;
    int generatorType;
    /**
     * @since v975
     */
    int dimensionType;
    /**
     * @since v2168
     */
    UUID packId;
    /**
     * @since v2192
     */
    String defaultBiome;
}

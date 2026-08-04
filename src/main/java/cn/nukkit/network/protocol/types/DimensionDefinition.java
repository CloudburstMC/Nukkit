package cn.nukkit.network.protocol.types;

import lombok.Value;

import java.util.UUID;

@Value
public class DimensionDefinition {

    String id;
    int maximumHeight;
    int minimumHeight;
    int generatorType;
    int dimensionType;
    UUID packId;
}

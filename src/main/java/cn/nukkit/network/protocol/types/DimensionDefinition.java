package cn.nukkit.network.protocol.types;

import lombok.Value;

@Value
public class DimensionDefinition {

    String id;
    int maximumHeight;
    int minimumHeight;
    int generatorType;
}

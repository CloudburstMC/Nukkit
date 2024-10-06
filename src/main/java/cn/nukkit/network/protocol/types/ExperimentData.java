package cn.nukkit.network.protocol.types;

import lombok.Data;

@Data
public class ExperimentData {
    private final String name;
    private final boolean enabled;
}

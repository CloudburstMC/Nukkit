package com.nukkitx.server.scoreboard;

import com.nukkitx.api.scoreboard.ObjectiveCriteria;
import lombok.Value;

@Value
public class NukkitObjectiveCriteria implements ObjectiveCriteria {
    private final String name;
    private final boolean readOnly;
}

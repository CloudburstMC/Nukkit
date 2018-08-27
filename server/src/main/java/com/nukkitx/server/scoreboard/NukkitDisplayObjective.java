package com.nukkitx.server.scoreboard;

import com.nukkitx.api.scoreboard.DisplayObjective;
import com.nukkitx.api.scoreboard.Objective;
import com.nukkitx.api.scoreboard.ObjectiveDisplaySlot;
import com.nukkitx.api.scoreboard.ObjectiveSortOrder;
import lombok.Value;

@Value
public class NukkitDisplayObjective implements DisplayObjective {
    private final Objective objective;
    private final ObjectiveDisplaySlot displaySlot;
    private final ObjectiveSortOrder sortOrder;
}

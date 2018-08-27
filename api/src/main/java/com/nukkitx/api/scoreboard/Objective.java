package com.nukkitx.api.scoreboard;

import com.nukkitx.api.Player;
import gnu.trove.list.TIntList;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.OptionalInt;

@ParametersAreNonnullByDefault
public interface Objective {

    String getName();

    String getDisplayName();

    void setDisplayName(String displayName);

    ObjectiveCriteria getCriteria();

    TIntList getScores();

    OptionalInt getScore(Scorer scorer);

    List<Player> getPlayers();
}

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

    List<Player> getPlayers();

    OptionalInt getScore(long id);

    void setScore(long id, int score);

    void modifyScore(long id, ModifyScoreFunction function);

    void resetScore(long id);
}

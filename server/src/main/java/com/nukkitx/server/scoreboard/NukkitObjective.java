package com.nukkitx.server.scoreboard;

import com.google.common.base.Preconditions;
import com.nukkitx.api.Player;
import com.nukkitx.api.scoreboard.*;
import com.nukkitx.server.level.manager.LevelScoreboardManager;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.hash.TLongIntHashMap;
import lombok.Data;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@Data
@ParametersAreNonnullByDefault
public class NukkitObjective implements Objective {
    private final TLongIntMap scores = new TLongIntHashMap();
    private final LevelScoreboardManager scoreboard;
    private final String name;
    private final ObjectiveCriteria criteria;
    private String displayName;

    LevelScoreboardManager getScoreboard() {
        return scoreboard;
    }

    @Override
    public void setDisplayName(String displayName) {
        Preconditions.checkNotNull(displayName, "displayName");
    }

    @Override
    public TIntList getScores() {
        return new TIntArrayList(scores.values());
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        scores.forEachKey(id -> {
            Optional<Scorer> scorer = scoreboard.getScorer(id);
            if (scorer.isPresent() && scorer.get() instanceof EntityScorer &&
                    ((EntityScorer) scorer.get()).getEntity() instanceof Player) {
                players.add((Player) ((EntityScorer) scorer.get()).getEntity());
            }
            return true;
        });

        return players;
    }

    @Override
    public OptionalInt getScore(long id) {
        if (scores.containsKey(id)) {
            return OptionalInt.of(scores.get(id));
        }
        return OptionalInt.empty();
    }

    @Override
    public void setScore(long id, int score) {
        scores.put(id, score);
        scoreboard.setScore(id, this, score);
    }

    @Override
    public void modifyScore(long id, ModifyScoreFunction function) {
        Preconditions.checkNotNull(function, "function");

        if (scores.containsKey(id)) {
            int score = scores.get(id);
            score = function.modify(score);
            scores.put(id, score);

            scoreboard.setScore(id, this, score);
        }
    }

    @Override
    public void resetScore(long id) {
        if (scores.containsKey(id)) {
            scoreboard.removeScore(id, this, scores.remove(id));
        }
    }
}

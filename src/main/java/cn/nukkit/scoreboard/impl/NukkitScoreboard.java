package cn.nukkit.scoreboard.impl;

import cn.nukkit.Server;
import cn.nukkit.player.Player;
import cn.nukkit.scoreboard.Score;
import cn.nukkit.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.ScoreboardObjective;
import com.nukkitx.protocol.bedrock.data.ScoreInfo;
import com.nukkitx.protocol.bedrock.packet.RemoveObjectivePacket;
import com.nukkitx.protocol.bedrock.packet.SetDisplayObjectivePacket;
import com.nukkitx.protocol.bedrock.packet.SetScorePacket;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NukkitScoreboard implements Scoreboard {

    private final Map<String, ScoreboardObjective> objectives = new HashMap<>();

    @Getter
    private final Set<Player> players = new HashSet<>();

    private NukkitScoreboard() {
    }

    @Override
    public ScoreboardObjective getObjective(String objectiveName) {
        return this.objectives.get(objectiveName);
    }

    @Override
    public void registerObjective(ScoreboardObjective objective) {
        this.objectives.put(objective.getName(), objective);

        ((NukkitScoreboardObjective) objective).scoreboard = this;

        SetDisplayObjectivePacket displayObjectivePacket = new SetDisplayObjectivePacket();
        displayObjectivePacket.setObjectiveId(objective.getName());
        displayObjectivePacket.setCriteria(objective.getCriteria().name().toLowerCase());
        displayObjectivePacket.setDisplayName(objective.getDisplayName());
        displayObjectivePacket.setDisplaySlot(objective.getDisplayMode().name().toLowerCase());
        displayObjectivePacket.setSortOrder(objective.getSortOrder().ordinal());
        Server.broadcastPacket(this.players, displayObjectivePacket);
    }

    @Override
    public void deregisterObjective(String name) {
        ScoreboardObjective objective = this.objectives.remove(name);

        RemoveObjectivePacket removeObjectivePacket = new RemoveObjectivePacket();
        removeObjectivePacket.setObjectiveId(objective.getName());
        Server.broadcastPacket(this.players, removeObjectivePacket);
    }

    public void show(Player player) {
        if (this.players.contains(player)) {
            return;
        }
        this.players.add(player);

        for (ScoreboardObjective objective : this.objectives.values()) {
            SetDisplayObjectivePacket displayObjectivePacket = new SetDisplayObjectivePacket();
            displayObjectivePacket.setObjectiveId(objective.getName());
            displayObjectivePacket.setCriteria(objective.getCriteria().name().toLowerCase());
            displayObjectivePacket.setDisplayName(objective.getDisplayName());
            displayObjectivePacket.setDisplaySlot(objective.getDisplayMode().name().toLowerCase());
            displayObjectivePacket.setSortOrder(objective.getSortOrder().ordinal());
            Server.broadcastPacket(this.players, displayObjectivePacket);

            List<ScoreInfo> scoreInfos = new ArrayList<>();
            for (Score<?> score : ((NukkitScoreboardObjective) objective).scores.values()) {
                scoreInfos.add(((NukkitScore<?>) score).createScoreInfo());
            }

            SetScorePacket setScorePacket = new SetScorePacket();
            setScorePacket.setAction(SetScorePacket.Action.SET);
            setScorePacket.setInfos(scoreInfos);
            Server.broadcastPacket(this.players, setScorePacket);
        }
    }

    public void hide(Player player) {
        if (!this.players.contains(player)) {
            return;
        }
        this.players.remove(player);

        for (ScoreboardObjective objective : this.objectives.values()) {
            RemoveObjectivePacket removeObjectivePacket = new RemoveObjectivePacket();
            removeObjectivePacket.setObjectiveId(objective.getName());
            player.sendPacket(removeObjectivePacket);
        }
    }

    public static ScoreboardBuilder providedBuilder() {
        return new NukkitScoreboardBuilder();
    }

    public static class NukkitScoreboardBuilder implements ScoreboardBuilder {

        private final NukkitScoreboard scoreboard = new NukkitScoreboard();

        @Override
        public ScoreboardBuilder objectives(ScoreboardObjective... objectives) {
            for (ScoreboardObjective objective : objectives) {
                this.scoreboard.objectives.put(objective.getName(), objective);
            }
            return this;
        }

        @Override
        public ScoreboardBuilder players(Player... players) {
            this.scoreboard.players.addAll(Arrays.asList(players));
            return this;
        }

        @Override
        public Scoreboard build() {
            return this.scoreboard;
        }
    }
}

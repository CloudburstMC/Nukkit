package cn.nukkit.utils;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.RemoveObjectivePacket;
import cn.nukkit.network.protocol.SetDisplayObjectivePacket;
import cn.nukkit.network.protocol.SetScorePacket;
import cn.nukkit.network.protocol.types.ScoreInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyScoreboard {

    public static long scoreCount = 0;

    private final List<Player> players = new ArrayList<>();
    private final Map<Long, ScoreInfo> scores = new HashMap<>();
    private final String objectiveId;

    private String displayName;
    private DisplaySlot displaySlot;
    private SortOrder sortOrder;

    public DummyScoreboard(String objectiveId, String displayName) {
        this(objectiveId, displayName, DisplaySlot.SIDEBAR, SortOrder.ASCENDING);
    }

    public DummyScoreboard(String objectiveId, String displayName, DisplaySlot displaySlot) {
        this(objectiveId, displayName, displaySlot, SortOrder.ASCENDING);
    }

    public DummyScoreboard(String objectiveId, String displayName, DisplaySlot displaySlot, SortOrder sortOrder) {
        this.objectiveId = objectiveId;

        this.displayName = displayName;
        this.displaySlot = displaySlot;
        this.sortOrder = sortOrder;
    }

    public void showTo(Player player) {
        if (this.players.contains(player)) {
            return;
        }

        SetDisplayObjectivePacket setDisplayObjectivePacket = new SetDisplayObjectivePacket();
        setDisplayObjectivePacket.criteria = "dummy";
        setDisplayObjectivePacket.displayName = this.displayName;
        setDisplayObjectivePacket.objectiveId = this.objectiveId;
        setDisplayObjectivePacket.displaySlot = this.displaySlot.name().toLowerCase();
        setDisplayObjectivePacket.sortOrder = this.sortOrder.ordinal();

        player.dataPacket(setDisplayObjectivePacket);

        SetScorePacket setScorePacket = new SetScorePacket();
        setScorePacket.action = SetScorePacket.Action.SET;
        setScorePacket.infos = this.scores.values().toArray(new ScoreInfo[0]);

        player.dataPacket(setScorePacket);

        this.players.add(player);
    }

    public void hideFrom(Player player) {
        if (!this.players.contains(player)) {
            return;
        }

        SetScorePacket setScorePacket = new SetScorePacket();
        setScorePacket.action = SetScorePacket.Action.REMOVE;
        setScorePacket.infos = this.scores.values().toArray(new ScoreInfo[0]);

        player.dataPacket(setScorePacket);

        RemoveObjectivePacket removeObjectivePacket = new RemoveObjectivePacket();
        removeObjectivePacket.objectiveId = this.objectiveId;

        player.dataPacket(removeObjectivePacket);

        this.players.remove(player);
    }

    public long addEntry(String displayText, int score) {
        long scoreboardId = DummyScoreboard.scoreCount++;

        ScoreInfo entry = new ScoreInfo();
        entry.type = ScoreInfo.ScorerType.FAKE;
        entry.score = score;
        entry.objectiveId = this.objectiveId;
        entry.name = displayText;
        entry.scoreboardId = scoreboardId;

        this.scores.put(scoreboardId, entry);

        SetScorePacket setScorePacket = new SetScorePacket();
        setScorePacket.action = SetScorePacket.Action.SET;
        setScorePacket.infos = new ScoreInfo[]{entry};

        for (Player player : this.players) {
            player.dataPacket(setScorePacket);
        }

        return scoreboardId;
    }

    public ScoreInfo getEntry(long scoreboardId) {
        return this.scores.get(scoreboardId);
    }

    public void removeEntry(long scoreboardId) {
        if (!this.scores.containsKey(scoreboardId)) {
            return;
        }

        SetScorePacket setScorePacket = new SetScorePacket();
        setScorePacket.action = SetScorePacket.Action.REMOVE;
        setScorePacket.infos = new ScoreInfo[]{this.scores.remove(scoreboardId)};

        for (Player player : this.players) {
            player.dataPacket(setScorePacket);
        }
    }

    private void resendToAll() {
        for (Player player : this.players.toArray(new Player[0])) {
            this.hideFrom(player);
            this.showTo(player);
        }
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public Map<Long, ScoreInfo> getScores() {
        return this.scores;
    }

    public String getObjectiveId() {
        return this.objectiveId;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.resendToAll();
    }

    public DisplaySlot getDisplaySlot() {
        return this.displaySlot;
    }

    public void setDisplaySlot(DisplaySlot displaySlot) {
        this.displaySlot = displaySlot;
        this.resendToAll();
    }

    public SortOrder getSortOrder() {
        return this.sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        this.resendToAll();
    }

}

package cn.nukkit.utils;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.RemoveObjectivePacket;
import cn.nukkit.network.protocol.SetDisplayObjectivePacket;
import cn.nukkit.network.protocol.SetScorePacket;
import cn.nukkit.network.protocol.types.ScoreEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyScoreboard {

    public static long scoreCount = 0;

    private final List<Player> players = new ArrayList<>();
    private final Map<Long, ScoreEntry> scores = new HashMap<>();
    private final String objectiveName;

    private String displayName;
    private DisplaySlot displaySlot;
    private SortOrder sortOrder;

    public DummyScoreboard(String objectiveName, String displayName) {
        this(objectiveName, displayName, DisplaySlot.SIDEBAR, SortOrder.ASCENDING);
    }

    public DummyScoreboard(String objectiveName, String displayName, DisplaySlot displaySlot) {
        this(objectiveName, displayName, displaySlot, SortOrder.ASCENDING);
    }

    public DummyScoreboard(String objectiveName, String displayName, DisplaySlot displaySlot, SortOrder sortOrder) {
        this.objectiveName = objectiveName;

        this.displayName = displayName;
        this.displaySlot = displaySlot;
        this.sortOrder = sortOrder;
    }

    public void addPlayer(Player player) {
        if (this.players.contains(player)) {
            return;
        }

        SetDisplayObjectivePacket setDisplayObjectivePacket = new SetDisplayObjectivePacket();
        setDisplayObjectivePacket.criteriaName = "dummy";
        setDisplayObjectivePacket.displayName = this.displayName;
        setDisplayObjectivePacket.objectiveName = this.objectiveName;
        setDisplayObjectivePacket.displaySlot = this.displaySlot.name().toLowerCase();
        setDisplayObjectivePacket.sortOrder = this.sortOrder.ordinal();

        player.dataPacket(setDisplayObjectivePacket);

        SetScorePacket setScorePacket = new SetScorePacket();
        setScorePacket.type = SetScorePacket.TYPE_CHANGE;
        setScorePacket.entries = this.scores.values().toArray(new ScoreEntry[0]);

        player.dataPacket(setScorePacket);

        this.players.add(player);
    }

    public void removePlayer(Player player) {
        if (!this.players.contains(player)) {
            return;
        }

        SetScorePacket setScorePacket = new SetScorePacket();
        setScorePacket.type = SetScorePacket.TYPE_REMOVE;
        setScorePacket.entries = this.scores.values().toArray(new ScoreEntry[0]);

        player.dataPacket(setScorePacket);

        RemoveObjectivePacket removeObjectivePacket = new RemoveObjectivePacket();
        removeObjectivePacket.objectiveName = this.objectiveName;

        player.dataPacket(removeObjectivePacket);

        this.players.remove(player);
    }

    public long addEntry(String displayText, int score) {
        long scoreId = DummyScoreboard.scoreCount++;

        ScoreEntry entry = new ScoreEntry();
        entry.type = ScoreEntry.TYPE_FAKE_PLAYER;
        entry.score = score;
        entry.objectiveName = this.objectiveName;
        entry.displayName = displayText;
        entry.scoreId = scoreId;

        this.scores.put(scoreId, entry);

        SetScorePacket setScorePacket = new SetScorePacket();
        setScorePacket.type = SetScorePacket.TYPE_CHANGE;
        setScorePacket.entries = new ScoreEntry[]{entry};

        for (Player player : this.players) {
            player.dataPacket(setScorePacket);
        }

        return scoreId;
    }

    public ScoreEntry getEntry(long scoreId) {
        return this.scores.get(scoreId);
    }

    public void removeEntry(long scoreId) {
        if (!this.scores.containsKey(scoreId)) {
            return;
        }

        SetScorePacket setScorePacket = new SetScorePacket();
        setScorePacket.type = SetScorePacket.TYPE_REMOVE;
        setScorePacket.entries = new ScoreEntry[]{this.scores.remove(scoreId)};

        for (Player player : this.players) {
            player.dataPacket(setScorePacket);
        }
    }

    private void resendToAll() {
        for (Player player : this.players.toArray(new Player[0])) {
            this.removePlayer(player);
            this.addPlayer(player);
        }
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public Map<Long, ScoreEntry> getScores() {
        return this.scores;
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

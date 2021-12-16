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
    private final Map<Long, String> lines = new HashMap<>();
    private final Map<Long, Integer> scores = new HashMap<>();
    private final String objectiveName;

    private String displayName;
    private DisplaySlot displaySlot;
    private SortOrder sortOrder;

    public DummyScoreboard(String displayName, String objectiveName) {
        this(displayName, objectiveName, DisplaySlot.SIDEBAR, SortOrder.ASCENDING);
    }

    public DummyScoreboard(String displayName, String objectiveName, DisplaySlot displaySlot) {
        this(displayName, objectiveName, displaySlot, SortOrder.ASCENDING);
    }

    public DummyScoreboard(String displayName, String objectiveName, DisplaySlot displaySlot, SortOrder sortOrder) {
        this.displayName = displayName;
        this.objectiveName = objectiveName;
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

        ScoreEntry[] entries = new ScoreEntry[this.lines.size()];

        int index = 0;
        for (long scoreId : this.lines.keySet()) {
            ScoreEntry entry = new ScoreEntry();
            entry.type = ScoreEntry.TYPE_FAKE_PLAYER;
            entry.score = this.scores.get(scoreId);
            entry.objectiveName = this.objectiveName;
            entry.displayName = this.lines.get(scoreId);
            entry.scoreId = scoreId;
            entries[index++] = entry;
        }

        SetScorePacket setScorePacket = new SetScorePacket();
        setScorePacket.type = SetScorePacket.TYPE_CHANGE;
        setScorePacket.entries = entries;

        player.dataPacket(setScorePacket);

        this.players.add(player);
    }

    public void removePlayer(Player player) {
        if (!this.players.contains(player)) {
            return;
        }

        ScoreEntry[] entries = new ScoreEntry[this.lines.size()];

        int index = 0;
        for (long scoreId : this.lines.keySet()) {
            ScoreEntry entry = new ScoreEntry();
            entry.type = ScoreEntry.TYPE_FAKE_PLAYER;
            entry.score = this.scores.get(scoreId);
            entry.objectiveName = this.objectiveName;
            entry.displayName = this.lines.get(scoreId);
            entry.scoreId = scoreId;
            entries[index++] = entry;
        }

        SetScorePacket setScorePacket = new SetScorePacket();
        setScorePacket.type = SetScorePacket.TYPE_REMOVE;
        setScorePacket.entries = entries;

        player.dataPacket(setScorePacket);

        RemoveObjectivePacket removeObjectivePacket = new RemoveObjectivePacket();
        removeObjectivePacket.objectiveName = this.objectiveName;

        player.dataPacket(removeObjectivePacket);

        this.players.remove(player);
    }

    public long addLine(int score, String lineContent) {
        long scoreId = DummyScoreboard.scoreCount++;

        this.lines.put(scoreId, lineContent);
        this.scores.put(scoreId, score);

        ScoreEntry entry = new ScoreEntry();
        entry.type = ScoreEntry.TYPE_FAKE_PLAYER;
        entry.score = score;
        entry.objectiveName = this.objectiveName;
        entry.displayName = lineContent;
        entry.scoreId = scoreId;

        SetScorePacket setScorePacket = new SetScorePacket();
        setScorePacket.type = SetScorePacket.TYPE_CHANGE;
        setScorePacket.entries = new ScoreEntry[]{entry};

        for (Player player : this.players) {
            player.dataPacket(setScorePacket);
        }

        return scoreId;
    }

    public String getLine(long scoreId) {
        return this.lines.get(scoreId);
    }

    public void removeLine(long scoreId) {
        if (!this.lines.containsKey(scoreId)) {
            return;
        }

        ScoreEntry entry = new ScoreEntry();
        entry.type = ScoreEntry.TYPE_FAKE_PLAYER;
        entry.score = this.scores.remove(scoreId);
        entry.objectiveName = this.objectiveName;
        entry.displayName = this.lines.remove(scoreId);
        entry.scoreId = scoreId;

        SetScorePacket setScorePacket = new SetScorePacket();
        setScorePacket.type = SetScorePacket.TYPE_REMOVE;
        setScorePacket.entries = new ScoreEntry[]{entry};

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

    public Map<Long, String> getLines() {
        return this.lines;
    }

    public Map<Long, Integer> getScores() {
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

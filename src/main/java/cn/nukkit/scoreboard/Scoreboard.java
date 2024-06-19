package cn.nukkit.scoreboard;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.RemoveObjectivePacket;
import cn.nukkit.network.protocol.SetDisplayObjectivePacket;
import cn.nukkit.network.protocol.SetScorePacket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple API which handles the networking side of fake player scoreboards while
 * leaving everything else to plugin developers
 *
 * @author PetteriM1
 */
@RequiredArgsConstructor
public class Scoreboard {

    /**
     * Used to generate unique identifiers for scoreboards
     */
    private static final AtomicInteger SCOREBOARD_COUNT = new AtomicInteger();

    /**
     * Scoreboard scores sorting mode
     */
    public enum SortOrder {
        ASCENDING,
        DESCENDING
    }

    /**
     * Scoreboard display location. Default is SIDEBAR.
     */
    @Getter
    @RequiredArgsConstructor
    public enum DisplaySlot {
        LIST("list"),
        SIDEBAR("sidebar"),
        BELOW_NAME("belowname");

        private final String type;
    }

    /**
     * Scoreboard score which has a unique id and modifiable score value
     */
    @AllArgsConstructor
    public static class Score {
        private final long id;
        @Getter
        private int score;
    }

    /**
     * Queued scoreboard score update
     */
    @AllArgsConstructor
    private static class QueuedScoreUpdate {
        private final String scorer;
        private final long currentScoreId;
        private final int currentScoreValue;
        private SetScorePacket.Action action;
    }

    /**
     * Players who can see the scoreboard. For internal use only, let plugin developers manage their scoreboards.
     */
    private final Set<Player> viewers = new HashSet<>();

    /**
     * Scorers and their scores
     */
    private final Map<String, Score> scores = new HashMap<>();

    /**
     * Scoreboard title
     */
    private final String title;

    /**
     * Order in which scorer names are sorted
     */
    private final SortOrder sortOrder;

    /**
     * Where the score is displayed
     */
    private final DisplaySlot displaySlot;

    /**
     * Unique identifier for the scoreboard
     */
    private final String objectiveId = String.valueOf(SCOREBOARD_COUNT.getAndIncrement());

    /**
     * Counter for unique score identifiers
     */
    private long scoreId;

    /**
     * Weather automatic sending of updated scores is disabled
     */
    private boolean isHoldingUpdates;

    /**
     * Queued score updates when holdUpdates is true
     */
    private final List<QueuedScoreUpdate> queuedUpdates = new ArrayList<>(0);

    /**
     * Update score for given scorer.
     * @param scorer scorer name / line text
     * @param newScore new score
     * @return true if a score was changed or removed
     */
    public boolean setScore(String scorer, int newScore) {
        Score score = this.scores.get(scorer);

        if (score == null) {
            score = new Score(++this.scoreId, newScore);
            this.scores.put(scorer, score);

            this.sendScore(scorer, score, SetScorePacket.Action.SET);
        } else if (score.score != newScore) {
            score.score = newScore;

            this.sendScore(scorer, score, SetScorePacket.Action.SET);

            return true;
        }

        return false;
    }

    /**
     * Remove score of given scorer.
     * @param scorer scorer name / line text
     * @return true if score did exist
     */
    public boolean removeScore(String scorer) {
        Score oldScore = this.scores.remove(scorer);

        if (oldScore != null) {
            this.sendScore(scorer, oldScore, SetScorePacket.Action.REMOVE);

            return true;
        }

        return false;
    }

    /**
     * Remove all scores and scorer names from the scoreboard.
     * @return true if scores did exist
     */
    public boolean clear() {
        if (this.scores.isEmpty()) {
            return false;
        }

        this.sendClearPacket(); // Call this before clear so we know what to remove

        this.scores.clear();
        return true;
    }

    /**
     * Show the scoreboard to a player. Remember to call hideFor(player) when the player quits.
     * @param player player
     * @return true if player did not see the scoreboard already
     */
    public boolean showTo(Player player) {
        if (this.viewers.add(player)) {
            this.sendShowPacket(player);
            return true;
        }

        return false;
    }

    /**
     * Hide the scoreboard for a player.
     * @param player player
     * @return true if player did see the scoreboard
     */
    public boolean hideFor(Player player) {
        if (this.viewers.remove(player)) {
            if (!player.isClosed()) {
                this.sendHidePacket(player);
            }
            return true;
        }

        return false;
    }

    /**
     * Pause automatic sending of score updates to allow efficient bulk modifications. Queued updates are sent on unholdUpdates().
     * @return true if successful, false if already on hold
     */
    public boolean holdUpdates() {
        if (this.isHoldingUpdates) {
            return false;
        }

        this.isHoldingUpdates = true;
        return true;
    }

    /**
     * Send all queued updates and continue sending updates automatically.
     * @return true if successful, false if not on hold
     */
    public boolean unholdUpdates() {
        if (!this.isHoldingUpdates) {
            return false;
        }

        this.isHoldingUpdates = false;

        SetScorePacket pk = null;
        SetScorePacket.Action lastAction = null;

        for (QueuedScoreUpdate update : this.queuedUpdates) {
            if (update.action != lastAction) {
                if (pk != null) {
                    Server.broadcastPacket(this.viewers, pk);
                }
                pk = new SetScorePacket();
            }
            lastAction = update.action;
            pk.action = update.action;
            pk.infos.add(new SetScorePacket.ScoreInfo(update.currentScoreId, this.objectiveId, update.currentScoreValue, update.scorer));
        }

        if (pk != null) {
            Server.broadcastPacket(this.viewers, pk);
        }

        this.queuedUpdates.clear();
        return true;
    }

    /**
     * Returns unmodifiable view of internal scorers and scores. Plugin developers who are making their plugin to
     * only display text can use this information to efficiently update only changed lines.
     * @return unmodifiable map
     */
    public Map<String, Score> getScores() {
        return Collections.unmodifiableMap(this.scores);
    }

    /**
     * Send updated score to viewers.
     * @param scorer scorer
     * @param score score
     * @param action set or remove
     */
    private void sendScore(String scorer, Score score, SetScorePacket.Action action) {
        if (this.viewers.isEmpty()) {
            return;
        }

        if (this.isHoldingUpdates) {
            this.queuedUpdates.add(new QueuedScoreUpdate(scorer, score.id, score.score, action));
            return;
        }

        SetScorePacket pk = new SetScorePacket();
        pk.action = action;
        pk.infos.add(new SetScorePacket.ScoreInfo(score.id, this.objectiveId, score.score, scorer));
        Server.broadcastPacket(this.viewers, pk);
    }

    /**
     * Send cleared current scoreboard to viewers.
     */
    private void sendClearPacket() {
        if (this.viewers.isEmpty()) {
            return;
        }

        if (this.isHoldingUpdates) {
            for (Map.Entry<String, Score> entry : this.scores.entrySet()) {
                String scorer = entry.getKey();
                Score score = entry.getValue();
                this.queuedUpdates.add(new QueuedScoreUpdate(scorer, score.id, score.score, SetScorePacket.Action.REMOVE));
            }
            return;
        }

        SetScorePacket pk = new SetScorePacket();
        pk.action = SetScorePacket.Action.REMOVE;
        for (Map.Entry<String, Score> entry : this.scores.entrySet()) {
            String scorer = entry.getKey();
            Score score = entry.getValue();
            pk.infos.add(new SetScorePacket.ScoreInfo(score.id, this.objectiveId, score.score, scorer));
        }
        Server.broadcastPacket(this.viewers, pk);
    }

    /**
     * Send scoreboard creation to the player
     * @param player player
     */
    private void sendShowPacket(Player player) {
        SetDisplayObjectivePacket objectivePacket = new SetDisplayObjectivePacket();
        objectivePacket.displaySlot = this.displaySlot;
        objectivePacket.objectiveId = this.objectiveId;
        objectivePacket.displayName = this.title;
        objectivePacket.criteria = "dummy";
        objectivePacket.sortOrder = this.sortOrder;
        player.dataPacket(objectivePacket);

        SetScorePacket scorePacket = new SetScorePacket();
        scorePacket.action = SetScorePacket.Action.SET;
        for (Map.Entry<String, Score> entry : this.scores.entrySet()) {
            String scorer = entry.getKey();
            Score score = entry.getValue();
            scorePacket.infos.add(new SetScorePacket.ScoreInfo(score.id, this.objectiveId, score.score, scorer));
        }
        player.dataPacket(scorePacket);
    }

    /**
     * Send scoreboard removal to the player
     * @param player player
     */
    private void sendHidePacket(Player player) {
        RemoveObjectivePacket pk = new RemoveObjectivePacket();
        pk.objectiveId = this.objectiveId;
        player.dataPacket(pk);
    }
}

package cn.nukkit.scoreboard;

import cn.nukkit.scoreboard.impl.NukkitScoreboardObjective;

import javax.annotation.Nullable;

/**
 * Represents an objective in a {@link Scoreboard}.
 */
public interface ScoreboardObjective {

    /**
     * Gets the name of this objective
     *
     * @return the name of this objective
     */
    String getName();

    /**
     * Gets the {@link DisplayMode} of this objective
     *
     * @return the display mode of this objective
     */
    DisplayMode getDisplayMode();

    /**
     * Gets the {@link SortOrder} of this objective
     *
     * @return the sort order of this objective
     */
    SortOrder getSortOrder();

    /**
     * Sets the {@link SortOrder} of the {@link Score}s
     * in this objective
     *
     * @param sortOrder the sort order of all the scores
     */
    void setSortOrder(SortOrder sortOrder);

    /**
     * Gets the display name of this objective
     *
     * @return the display name of this objective
     */
    String getDisplayName();

    /**
     * Sets the display name of this objective
     *
     * @param displayName the display name of this objective
     */
    void setDisplayName(String displayName);

    /**
     * Gets the {@link ScoreboardCriteria} of this objective
     *
     * @return the criteria of this objective
     */
    ScoreboardCriteria getCriteria();

    /**
     * Gets the {@link Score} from the specified name
     *
     * @param name the name of the score
     * @return the score from the specified name
     */
    @Nullable
    Score<?> getScore(String name);

    /**
     * Gets the {@link Score} with the specified {@link ScoreType}
     *
     * @param scoreType the score type
     * @param <T> the value
     * @return the score with the specified score type
     */
    @Nullable
    <T> Score<T> getScore(String name, ScoreType<T> scoreType);

    /**
     * Gets or creates a {@link Score} with the specified name,
     * {@link ScoreType}, and input
     *
     * @param name the name of the score
     * @param type the score type
     * @param input the input of the score
     * @param value the value of the score
     * @param <T> the value
     * @return the score with the specified name, scoretype and input
     */
    <T> Score<T> getOrCreateScore(String name, ScoreType<T> type, T input, int value);

    /**
     * Removes the {@link Score} with the specified name
     *
     * @param name the name of the score
     */
    void removeScore(String name);

    /**
     * Registers a score
     *
     * @param score the score to register
     */
    void createScore(Score<?> score);

    /**
     * Creates a new instance of a {@link ScoreboardObjectiveBuilder}
     *
     * @return a new instance of an objective builder
     */
    static ScoreboardObjectiveBuilder builder() {
        return NukkitScoreboardObjective.providedBuilder();
    }

    interface ScoreboardObjectiveBuilder {

        ScoreboardObjectiveBuilder name(String name);

        ScoreboardObjectiveBuilder displayMode(DisplayMode displayMode);

        ScoreboardObjectiveBuilder sortOrder(SortOrder sortOrder);

        ScoreboardObjectiveBuilder displayName(String displayName);

        ScoreboardObjectiveBuilder criteria(ScoreboardCriteria criteria);

        ScoreboardObjectiveBuilder scores(Score<?>... scores);

        ScoreboardObjective build();
    }
}

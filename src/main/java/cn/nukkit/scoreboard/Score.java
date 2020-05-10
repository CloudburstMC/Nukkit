package cn.nukkit.scoreboard;

import cn.nukkit.scoreboard.impl.NukkitScore;

import javax.annotation.Nullable;

/**
 * Represents a score for a {@link ScoreboardObjective}.
 */
public interface Score<T> {

    /**
     * Gets the {@link ScoreboardObjective} of
     * this score
     *
     * @return the scoreboard objective of this score
     */
    ScoreboardObjective getObjective();

    /**
     * Gets the amount
     *
     * @return the amount
     */
    int getAmount();

    /**
     * Sets the amount
     *
     * @param value the amount
     */
    void setAmount(int value);

    /**
     * Gets the name of this score
     *
     * @return the name of this score
     */
    String getName();

    /**
     * Gets the type of score this is
     *
     * @return the type of score this is
     */
    ScoreType<T> getScoreType();

    /**
     * Gets the value of this score
     *
     * @return the value of this score
     */
    T getValue();

    /**
     * Sets the value of the score
     *
     * @param value the value of the score
     */
    void setValue(T value);

    /**
     * Creates a new instance of a {@link ScoreBuilder}
     *
     * @param scoreType the score type
     * @param <U> the value
     * @return a new instance of a score builder
     */
    static <U> ScoreBuilder<U> builder(ScoreType<U> scoreType) {
        return NukkitScore.providedBuilder(scoreType);
    }

    interface ScoreBuilder<T> {

        ScoreBuilder<T> amount(int amount);

        ScoreBuilder<T> name(String name);

        ScoreBuilder<T> value(T value);

        Score<T> build();
    }
}

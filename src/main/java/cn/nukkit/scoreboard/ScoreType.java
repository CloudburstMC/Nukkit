package cn.nukkit.scoreboard;

import cn.nukkit.entity.Entity;
import cn.nukkit.player.Player;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Represents the score type for the
 * {@link Score} in a {@link Scoreboard}.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScoreType<T> {

    /**
     * A score type where the value is
     * that of a player.
     */
    public static final ScoreType<Player> PLAYER = new ScoreType<>("player", Player.class);

    /**
     * A score type where the value is
     * that of an entity.
     */
    public static final ScoreType<Entity> ENTITY = new ScoreType<>("entity", Entity.class);

    /**
     * A fake score type that does not
     * require an entity or player.
     */
    public static final ScoreType<String> FAKE = new ScoreType<>("fake", String.class);

    private String name;
    private Class<T> supportedType;

    /**
     * Gets the supported value type for
     * this score type
     *
     * @return the supported value for this score type
     */
    public Class<T> getSupportedType() {
        return supportedType;
    }

    @Override
    public String toString() {
        return name;
    }
}

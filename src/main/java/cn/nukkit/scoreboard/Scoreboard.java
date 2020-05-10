package cn.nukkit.scoreboard;

import cn.nukkit.player.Player;
import cn.nukkit.scoreboard.impl.NukkitScoreboard;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Represents a scoreboard in the game, which can contain
 * various information and be used to display data for players.
 */
public interface Scoreboard {

    /**
     * Gets the {@link ScoreboardObjective} with the specified name
     *
     * @param objectiveName the name of the objective
     * @return the objective with the specified name
     */
    @Nullable
    ScoreboardObjective getObjective(String objectiveName);

    /**
     * Registers a new {@link ScoreboardObjective} to the scoreboard
     * @param objective the objective
     */
    void registerObjective(ScoreboardObjective objective);

    /**
     * Deregisters a {@link ScoreboardObjective} from the scoreboard.
     *
     * @param name the name of the objective
     */
    void deregisterObjective(String name);

    /**
     * Creates a new instance of a {@link ScoreboardBuilder}
     *
     * @return a new instance of a scoreboard builder
     */
    static ScoreboardBuilder builder() {
        return NukkitScoreboard.providedBuilder();
    }

    interface ScoreboardBuilder {

        ScoreboardBuilder objectives(ScoreboardObjective... objectives);

        ScoreboardBuilder players(Player... players);

        default ScoreboardBuilder players(Collection<Player> players) {
            return this.players(players.toArray(new Player[0]));
        }

        Scoreboard build();
    }
}

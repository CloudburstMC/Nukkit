package cn.nukkit.scoreboard;

/**
 * Represents where a {@link ScoreboardObjective} should be displayed
 * on a {@link Scoreboard}.
 */
public enum DisplayMode {

    /**
     * Display mode that shows the objective
     * in the player list.
     */
    LIST,

    /**
     * Display mode that shows the objective
     * ion the sidebar.
     */
    SIDEBAR,

    /**
     * Display mode that shows the objective
     * below the player's name.
     */
    BELOWNAME
}

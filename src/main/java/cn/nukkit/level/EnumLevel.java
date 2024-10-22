package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.level.generator.Generator;

/**
 * Default dimensions and their Levels
 */
public enum EnumLevel {

    OVERWORLD,
    NETHER,
    THE_END;

    private Level level;

    /**
     * Get Level
     *
     * @return Level or null if the dimension is not enabled
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Internal: Initialize default overworld, nether and the end Levels
     */
    public static void initLevels() {
        Server server = Server.getInstance();
        OVERWORLD.level = server.getDefaultLevel();
        if (server.getPropertyBoolean("allow-nether", true)) {
            if (server.getLevelByName("nether") == null) {
                server.generateLevel("nether", System.currentTimeMillis(), Generator.getGenerator(Generator.TYPE_NETHER));
                server.loadLevel("nether");
            }
            NETHER.level = server.getLevelByName("nether");
        }
        if (server.getPropertyBoolean("allow-the-end", true)) {
            if (server.getLevelByName("the_end") == null) {
                server.generateLevel("the_end", System.currentTimeMillis(), Generator.getGenerator(Generator.TYPE_THE_END));
                server.loadLevel("the_end");
            }
            THE_END.level = server.getLevelByName("the_end");
        }
    }

    static int mRound32(int value) {
        return Math.round((float) value / 32) * 32;
    }
}

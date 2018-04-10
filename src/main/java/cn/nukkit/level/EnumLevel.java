package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.Generator;

public enum EnumLevel {
    OVERWORLD,
    NETHER,
    //THE_END
    ;

    Level level;

    public Level getLevel() {
        return level;
    }

    public static void initLevels() {
        OVERWORLD.level = Server.getInstance().getDefaultLevel();
        NETHER.level = Server.getInstance().getLevelByName("nether");

        if (NETHER.level == null && Server.getInstance().isNetherAllowed())   {
            // Nether is allowed, and not found, create the default nether world
            Server.getInstance().getLogger().info("No level called \"nether\" found, creating default nether level.");

            // Generate seed for nether and get nether generator
            long seed = System.currentTimeMillis();
            Class<? extends Generator> generator = Generator.getGenerator("nether");

            // Generate the nether world
            Server.getInstance().generateLevel("nether", seed, generator);

            // Finally, load the level if not already loaded
            if (!Server.getInstance().isLevelLoaded("nether")) {
                Server.getInstance().loadLevel("nether");
            }

        } else if (NETHER.level == null && !Server.getInstance().isNetherAllowed()) {
            // don't load nether world
            Server.getInstance().getLogger().alert("No level called \"nether\" found or nether is disabled in server properties! Nether functionality will be disabled.");
        } else if (NETHER.level != null && !Server.getInstance().isNetherAllowed()) {
            // don't load the nether world
            Server.getInstance().getLogger().alert("No level called \"nether\" found or nether is disabled in server properties! Nether functionality will be disabled.");
        }
    }

    public static Level getOtherNetherPair(Level current)   {
        if (current == OVERWORLD.level) {
            return NETHER.level;
        } else if (current == NETHER.level) {
            return OVERWORLD.level;
        } else {
            throw new IllegalArgumentException("Neither overworld nor nether given!");
        }
    }

    public static Position moveToNether(Position current)   {
        if (NETHER.level == null) {
            return null;
        } else {
            if (current.level == OVERWORLD.level) {
                return new Position(mRound(current.getFloorX() >> 3, 128), mRound(current.getFloorY(), 32), mRound(current.getFloorZ() >> 3, 128), NETHER.level);
            } else if (current.level == NETHER.level) {
                return new Position(mRound(current.getFloorX() << 3, 1024), mRound(current.getFloorY(), 32), mRound(current.getFloorZ() << 3, 1024), OVERWORLD.level);
            } else {
                throw new IllegalArgumentException("Neither overworld nor nether given!");
            }
        }
    }

    private static final int mRound(int value, int factor) {
        return Math.round(value / factor) * factor;
    }
}

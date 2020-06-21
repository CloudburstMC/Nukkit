package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.level.generator.Generator;

public enum EnumLevel {
    OVERWORLD,
    NETHER,
    //THE_END
    ;

    Level level;

    public static void initLevels() {
        EnumLevel.OVERWORLD.level = Server.getInstance().getDefaultLevel();

        // attempt to load the nether world if it is allowed in server properties
        if (Server.getInstance().isNetherAllowed() && !Server.getInstance().loadLevel("nether")) {

            // Nether is allowed, and not found, create the default nether world
            Server.getInstance().getLogger().info("No level called \"nether\" found, creating default nether level.");

            // Generate seed for nether and get nether generator
            final long seed = System.currentTimeMillis();
            final Class<? extends Generator> generator = Generator.getGenerator("nether");

            // Generate the nether world
            Server.getInstance().generateLevel("nether", seed, generator);

            // Finally, load the level if not already loaded and set the level
            if (!Server.getInstance().isLevelLoaded("nether")) {
                Server.getInstance().loadLevel("nether");
            }

        }

        EnumLevel.NETHER.level = Server.getInstance().getLevelByName("nether");

        if (EnumLevel.NETHER.level == null) {
            // Nether is not found or disabled
            Server.getInstance().getLogger().alert("No level called \"nether\" found or nether is disabled in server properties! Nether functionality will be disabled.");
        }
    }

    public static Level getOtherNetherPair(final Level current) {
        if (current == EnumLevel.OVERWORLD.level) {
            return EnumLevel.NETHER.level;
        } else if (current == EnumLevel.NETHER.level) {
            return EnumLevel.OVERWORLD.level;
        } else {
            throw new IllegalArgumentException("Neither overworld nor nether given!");
        }
    }

    public static Position moveToNether(final Position current) {
        if (EnumLevel.NETHER.level == null) {
            return null;
        } else {
            if (current.level == EnumLevel.OVERWORLD.level) {
                return new Position(EnumLevel.mRound(current.getFloorX() >> 3, 128), EnumLevel.mRound(current.getFloorY(), 32), EnumLevel.mRound(current.getFloorZ() >> 3, 128), EnumLevel.NETHER.level);
            } else if (current.level == EnumLevel.NETHER.level) {
                return new Position(EnumLevel.mRound(current.getFloorX() << 3, 1024), EnumLevel.mRound(current.getFloorY(), 32), EnumLevel.mRound(current.getFloorZ() << 3, 1024), EnumLevel.OVERWORLD.level);
            } else {
                throw new IllegalArgumentException("Neither overworld nor nether given!");
            }
        }
    }

    private static final int mRound(final int value, final int factor) {
        return Math.round((float) value / factor) * factor;
    }

    public Level getLevel() {
        return this.level;
    }
}

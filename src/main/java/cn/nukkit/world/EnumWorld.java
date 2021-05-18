package cn.nukkit.world;

import cn.nukkit.Server;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.world.generator.Generator;

public enum EnumWorld {
    OVERWORLD,
    NETHER,
    //THE_END
    ;

    World world;

    public World getWorld() {
        return world;
    }

    public static void initWorlds() {
        OVERWORLD.world = Server.getInstance().getDefaultWorld();

        // attempt to load the nether world if it is allowed in server properties
        if (Server.getInstance().isNetherAllowed() && !Server.getInstance().loadWorld("nether")) {

            // Nether is allowed, and not found, create the default nether world
            Server.getInstance().getLogger().info("No world called \"nether\" found, creating default nether.");

            // Generate seed for nether and get nether generator
            long seed = System.currentTimeMillis();
            Class<? extends Generator> generator = Generator.getGenerator("nether");

            // Generate the nether world
            Server.getInstance().generateWorld("nether", seed, generator);

            // Finally, load the world if not already loaded and set the world
            if (!Server.getInstance().isWorldLoaded("nether")) {
                Server.getInstance().loadWorld("nether");
            }

        }

        NETHER.world = Server.getInstance().getWorldByName("nether");

        if (NETHER.world == null) {
            // Nether is not found or disabled
            Server.getInstance().getLogger().alert("No level called \"nether\" found or nether is disabled in server properties! Nether functionality will be disabled.");
        }
    }

    public static World getOtherNetherPair(World current)   {
        if (current == OVERWORLD.world) {
            return NETHER.world;
        } else if (current == NETHER.world) {
            return OVERWORLD.world;
        } else {
            throw new IllegalArgumentException("Neither overworld nor nether given!");
        }
    }

    public static Position moveToNether(Position current)   {
        if (NETHER.world == null) {
            return null;
        } else {
            if (current.world == OVERWORLD.world) {
                return new Position(mRound(current.getFloorX() >> 3, 128), NukkitMath.clamp(mRound(current.getFloorY(), 32), 70, 128 - 10), mRound(current.getFloorZ() >> 3, 128), NETHER.world);
            } else if (current.world == NETHER.world) {
                return new Position(mRound(current.getFloorX() << 3, 1024), NukkitMath.clamp(mRound(current.getFloorY(), 32), 70, 256 - 10), mRound(current.getFloorZ() << 3, 1024), OVERWORLD.world);
            } else {
                throw new IllegalArgumentException("Neither overworld nor nether given!");
            }
        }
    }

    private static final int mRound(int value, int factor) {
        return Math.round((float) value / factor) * factor;
    }
}

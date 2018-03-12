package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;

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
        if (NETHER.level == null)   {
            Server.getInstance().getLogger().alert("No level called \"nether\" found! Nether functionality will be disabled.");
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

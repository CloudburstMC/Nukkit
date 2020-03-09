package cn.nukkit.level;

import cn.nukkit.Server;
import com.nukkitx.math.vector.Vector3f;
import lombok.extern.log4j.Log4j2;

@Log4j2
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

        Level netherLevel = Server.getInstance().getLevelByName("nether");
        // attempt to load the nether world if it is allowed in server properties
        if (netherLevel != null && Server.getInstance().isNetherAllowed()) {
            NETHER.level = netherLevel;
        } else {
            // Nether is not found or disabled
            log.warn("No level called \"nether\" found or nether is disabled in server properties! Nether functionality will be disabled.");
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

    public static Location moveToNether(Location current) {
        if (NETHER.level == null) {
            return null;
        } else {
            int x, y, z;
            Level level;
            if (current.getLevel() == OVERWORLD.level) {
                x = mRound(current.getFloorX() >> 3, 128);
                y = mRound(current.getFloorY(), 32);
                z = mRound(current.getFloorZ() >> 3, 128);
                level = NETHER.level;
            } else if (current.getLevel() == NETHER.level) {
                x = mRound(current.getFloorX() << 3, 1024);
                y = mRound(current.getFloorY(), 32);
                z = mRound(current.getFloorZ() << 3, 1024);
                level = OVERWORLD.level;
            } else {
                throw new IllegalArgumentException("Neither overworld nor nether given!");
            }
            return Location.from(Vector3f.from(x, y, z), current.getYaw(), current.getPitch(), level);
        }
    }

    private static final int mRound(int value, int factor) {
        return Math.round((float) value / factor) * factor;
    }
}

package cn.nukkit.level;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Location extends Position {

    public double yaw;
    public double pitch;

    public Location() {
        this(0);
    }

    public Location(int x) {
        this(x, 0);
    }

    public Location(int x, int y) {
        this(x, y, 0);
    }

    public Location(int x, int y, int z) {
        this(x, y, z, 0.0);
    }

    public Location(int x, int y, int z, double yaw) {
        this(x, y, z, yaw, 0.0);
    }

    public Location(int x, int y, int z, double yaw, double pitch) {
        this(x, y, z, yaw, pitch, null);
    }

    public Location(int x, int y, int z, double yaw, double pitch, Level level) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.level = level;
    }

    public double getYaw() {
        return this.yaw;
    }

    public double getPitch() {
        return this.pitch;
    }

    public String __toString() {
        return "Location (level=" + (this.isValid() ? this.getLevel().getName() : "null") + ", x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", yaw=" + this.yaw + ", pitch=" + this.pitch + ")";
    }
}

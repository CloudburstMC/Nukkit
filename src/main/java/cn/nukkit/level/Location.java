package cn.nukkit.level;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.LevelException;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Location extends Vector3{

    public double yaw;
    public double pitch;
    public Level level;

    @Deprecated
    public Location() {
        this(0);
    }

    @Deprecated
    public Location(double x) {
        this(x, 0);
    }

    @Deprecated
    public Location(double x, double y) {
        this(x, y, 0);
    }

    @Deprecated
    public Location(double x, double y, double z) {
        this(x, y, z, 0);
    }

    @Deprecated
    public Location(double x, double y, double z, double yaw) {
        this(x, y, z, yaw, 0);
    }

    @Deprecated
    public Location(double x, double y, double z, double yaw, double pitch) {
        this(x, y, z, yaw, pitch, null);
    }

    @Deprecated
    public Location(double x, double y, double z, double yaw, double pitch, Level level) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.level = level;
    }

    ////////////////
    public Location(Level level, double x, double y, double z) {
        this(level, x, y, z, 0, 0);
    }

    public Location(Level level, double x, double y, double z, double yaw, double pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.level = level;
    }

    public static Location fromObject(Vector3 pos) {
        return fromObject(pos, null, 0.0f, 0.0f);
    }

    public static Location fromObject(Vector3 pos, Level level) {
        return fromObject(pos, level, 0.0f, 0.0f);
    }

    public static Location fromObject(Vector3 pos, Level level, float yaw) {
        return fromObject(pos, level, yaw, 0.0f);
    }

    public static Location fromObject(Vector3 pos, Level level, float yaw, float pitch) {
        return new Location(pos.x, pos.y, pos.z, yaw, pitch, level);
    }

    public double getYaw() {
        return this.yaw;
    }

    public double getPitch() {
        return this.pitch;
    }

    @Override
    public String toString() {
        return "Location (level=" + (this.isValid() ? this.getLevel().getName() : "null") + ", x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", yaw=" + this.yaw + ", pitch=" + this.pitch + ")";
    }

    public boolean isValid() {
        return this.level != null;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public Location setComponents(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override
    public Location getSide(int side) {
        return this.getSide(side, 1);
    }

    @Override
    public Location getSide(int side, int step) {
        if (!this.isValid()) throw new LevelException("Undefined Level reference");
        return Location.fromObject(super.getSide(side, step), this.level);
    }

    @Override
    public Location add(double x, double y) {
        return this.add(x, y, 0);
    }
    @Override
    public Location add(double x, double y, double z) {
        return new Location(this.level, this.x + x, this.y + y, this.z + z,this.yaw, this.pitch);
    }
    @Override
    public Location add(Vector3 x) {
        return new Location(this.level, this.x + x.getX(), this.y + x.getY(), this.z + x.getZ(), this.yaw, this.pitch);
    }
    @Override
    public Location subtract() {
        return this.subtract(0, 0, 0);
    }
    @Override
    public Location subtract(double x) {
        return this.subtract(x, 0, 0);
    }
    @Override
    public Location subtract(double x, double y) {
        return this.subtract(x, y, 0);
    }
    @Override
    public Location subtract(double x, double y, double z) {
        return this.add(-x, -y, -z);
    }
    @Override
    public Location subtract(Vector3 x) {
        return this.add(-x.getX(), -x.getY(), -x.getZ());
    }


}

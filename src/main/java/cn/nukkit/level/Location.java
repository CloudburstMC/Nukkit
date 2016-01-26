package cn.nukkit.level;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.LevelException;

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

    public Location(double x) {
        this(x, 0);
    }

    public Location(double x, double y) {
        this(x, y, 0);
    }

    public Location(double x, double y, double z) {
        this(x, y, z, 0);
    }

    public Location(double x, double y, double z, double yaw) {
        this(x, y, z, yaw, 0);
    }

    public Location(double x, double y, double z, double yaw, double pitch) {
        this(x, y, z, yaw, pitch, null);
    }

    public Location(double x, double y, double z, double yaw, double pitch, Level level) {
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
        return new Location(pos.x, pos.y, pos.z, yaw, pitch, (level == null) ? ((pos instanceof Position) ? ((Position) pos).level : null) : level);
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

    @Override
    public Location getLocation() {
        if (this.isValid()) return new Location(this.x, this.y, this.z, this.yaw, this.pitch, this.level);
        else throw new LevelException("Undefined Level reference");
    }

    @Override
    public Location add(double x) {
        return this.add(x, 0, 0);
    }

    @Override
    public Location add(double x, double y) {
        return this.add(x, y, 0);
    }

    @Override
    public Location add(double x, double y, double z) {
        return new Location(this.x + x, this.y + y, this.z + z, this.yaw, this.pitch, this.level);
    }

    @Override
    public Location add(Vector3 x) {
        return new Location(this.x + x.getX(), this.y + x.getY(), this.z + x.getZ(), this.yaw, this.pitch, this.level);
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

    @Override
    public Location multiply(double number) {
        return new Location(this.x * number, this.y * number, this.z * number, this.yaw, this.pitch, this.level);
    }

    @Override
    public Location divide(double number) {
        return new Location(this.x / number, this.y / number, this.z / number, this.yaw, this.pitch, this.level);
    }

    @Override
    public Location ceil() {
        return new Location((int) Math.ceil(this.x), (int) Math.ceil(this.y), (int) Math.ceil(this.z), this.yaw, this.pitch, this.level);
    }

    @Override
    public Location floor() {
        return new Location(this.getFloorX(), this.getFloorY(), this.getFloorZ(), this.yaw, this.pitch, this.level);
    }

    @Override
    public Location round() {
        return new Location(Math.round(this.x), Math.round(this.y), Math.round(this.z), this.yaw, this.pitch, this.level);
    }

    @Override
    public Location abs() {
        return new Location((int) Math.abs(this.x), (int) Math.abs(this.y), (int) Math.abs(this.z), this.yaw, this.pitch, this.level);
    }

}

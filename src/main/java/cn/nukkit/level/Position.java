package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.LevelException;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Position extends Vector3 {
    public Level level;

    public Position() {
        this(0, 0, 0, null);
    }

    public Position(double x) {
        this(x, 0, 0, null);
    }

    public Position(double x, double y) {
        this(x, y, 0, null);
    }

    public Position(double x, double y, double z) {
        this(x, y, z, null);
    }

    public Position(double x, double y, double z, Level level) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.level = level;
    }

    public static Position fromObject(Vector3 pos) {
        return fromObject(pos, null);
    }

    public static Position fromObject(Vector3 pos, Level level) {
        return new Position(pos.x, pos.y, pos.z, level);
    }

    public Level getLevel() {
        return this.level;
    }

    public Position setLevel(Level level) {
        this.level = level;
        return this;
    }

    public boolean isValid() {
        return this.level != null;
    }

    public boolean setStrong() {
        return false;
    }

    public boolean setWeak() {
        return false;
    }

    public Position getSide(BlockFace face) {
        return this.getSide(face, 1);
    }

    public Position getSide(BlockFace face, int step) {
        if (!this.isValid()) {
            throw new LevelException("Undefined Level reference");
        }
        return Position.fromObject(super.getSide(face, step), this.level);
    }

    @Override
    public String toString() {
        return "Position(level=" + (this.isValid() ? this.getLevel().getName() : "null") + ",x=" + this.x + ",y=" + this.y + ",z=" + this.z + ")";
    }

    @Override
    public Position setComponents(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Block getLevelBlock() {
        if (this.isValid()) return this.level.getBlock(this);
        else throw new LevelException("Undefined Level reference");
    }

    public Location getLocation() {
        if (this.isValid()) return new Location(this.x, this.y, this.z, 0, 0, this.level);
        else throw new LevelException("Undefined Level reference");
    }

    @Override
    public Position add(double x) {
        return this.add(x, 0, 0);
    }

    @Override
    public Position add(double x, double y) {
        return this.add(x, y, 0);
    }

    @Override
    public Position add(double x, double y, double z) {
        return new Position(this.x + x, this.y + y, this.z + z, this.level);
    }

    @Override
    public Position add(Vector3 x) {
        return new Position(this.x + x.getX(), this.y + x.getY(), this.z + x.getZ(), this.level);
    }

    @Override
    public Position subtract() {
        return this.subtract(0, 0, 0);
    }

    @Override
    public Position subtract(double x) {
        return this.subtract(x, 0, 0);
    }

    @Override
    public Position subtract(double x, double y) {
        return this.subtract(x, y, 0);
    }

    @Override
    public Position subtract(double x, double y, double z) {
        return this.add(-x, -y, -z);
    }

    @Override
    public Position subtract(Vector3 x) {
        return this.add(-x.getX(), -x.getY(), -x.getZ());
    }

    @Override
    public Position multiply(double number) {
        return new Position(this.x * number, this.y * number, this.z * number, this.level);
    }

    @Override
    public Position divide(double number) {
        return new Position(this.x / number, this.y / number, this.z / number, this.level);
    }

    @Override
    public Position ceil() {
        return new Position((int) Math.ceil(this.x), (int) Math.ceil(this.y), (int) Math.ceil(this.z), this.level);
    }

    @Override
    public Position floor() {
        return new Position(this.getFloorX(), this.getFloorY(), this.getFloorZ(), this.level);
    }

    @Override
    public Position round() {
        return new Position(Math.round(this.x), Math.round(this.y), Math.round(this.z), this.level);
    }

    @Override
    public Position abs() {
        return new Position((int) Math.abs(this.x), (int) Math.abs(this.y), (int) Math.abs(this.z), this.level);
    }

    @Override
    public Position clone() {
        return (Position) super.clone();
    }

    public FullChunk getChunk() {
        return isValid() ? level.getChunk(getChunkX(), getChunkZ()) : null;
    }
}

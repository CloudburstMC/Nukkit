package cn.nukkit.world;

import cn.nukkit.block.Block;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.WorldException;
import cn.nukkit.world.format.FullChunk;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Position extends Vector3 {
    public World world;

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

    public Position(double x, double y, double z, World level) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = level;
    }

    public static Position fromObject(Vector3 pos) {
        return fromObject(pos, null);
    }

    public static Position fromObject(Vector3 pos, World level) {
        return new Position(pos.x, pos.y, pos.z, level);
    }

    public World getWorld() {
        return this.world;
    }

    public Position setLevel(World world) {
        this.world = world;
        return this;
    }

    public boolean isValid() {
        return this.world != null;
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
            throw new WorldException("Undefined Level reference");
        }
        return Position.fromObject(super.getSide(face, step), this.world);
    }

    @Override
    public String toString() {
        return "Position(world=" + (this.isValid() ? this.getWorld().getName() : "null") + ",x=" + this.x + ",y=" + this.y + ",z=" + this.z + ")";
    }

    @Override
    public Position setComponents(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Block getLevelBlock() {
        if (this.isValid()) return this.world.getBlock(this);
        else throw new WorldException("Undefined World reference");
    }

    public Location getLocation() {
        if (this.isValid()) return new Location(this.x, this.y, this.z, 0, 0, this.world);
        else throw new WorldException("Undefined World reference");
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
        return new Position(this.x + x, this.y + y, this.z + z, this.world);
    }

    @Override
    public Position add(Vector3 x) {
        return new Position(this.x + x.getX(), this.y + x.getY(), this.z + x.getZ(), this.world);
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
        return new Position(this.x * number, this.y * number, this.z * number, this.world);
    }

    @Override
    public Position divide(double number) {
        return new Position(this.x / number, this.y / number, this.z / number, this.world);
    }

    @Override
    public Position ceil() {
        return new Position((int) Math.ceil(this.x), (int) Math.ceil(this.y), (int) Math.ceil(this.z), this.world);
    }

    @Override
    public Position floor() {
        return new Position(this.getFloorX(), this.getFloorY(), this.getFloorZ(), this.world);
    }

    @Override
    public Position round() {
        return new Position(Math.round(this.x), Math.round(this.y), Math.round(this.z), this.world);
    }

    @Override
    public Position abs() {
        return new Position((int) Math.abs(this.x), (int) Math.abs(this.y), (int) Math.abs(this.z), this.world);
    }

    @Override
    public Position clone() {
        return (Position) super.clone();
    }

    public FullChunk getChunk() {
        return isValid() ? world.getChunk(getChunkX(), getChunkZ()) : null;
    }
}

package cn.nukkit.math;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

public class BlockVector3 implements Cloneable {
    public int x;
    public int y;
    public int z;

    public BlockVector3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockVector3() {
    }

    public BlockVector3 setComponents(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public Vector3 add(double x) {
        return this.add(x, 0, 0);
    }

    public Vector3 add(double x, double y) {
        return this.add(x, y, 0);
    }

    public Vector3 add(double x, double y, double z) {
        return new Vector3(this.x + x, this.y + y, this.z + z);
    }

    public Vector3 add(Vector3 x) {
        return new Vector3(this.x + x.getX(), this.y + x.getY(), this.z + x.getZ());
    }

    public Vector3 subtract(double x) {
        return this.subtract(x, 0, 0);
    }

    public Vector3 subtract(double x, double y) {
        return this.subtract(x, y, 0);
    }

    public Vector3 subtract(double x, double y, double z) {
        return this.add(-x, -y, -z);
    }

    public Vector3 subtract(Vector3 x) {
        return this.add(-x.getX(), -x.getY(), -x.getZ());
    }

    public BlockVector3 add(int x) {
        return this.add(x, 0, 0);
    }

    public BlockVector3 add(int x, int y) {
        return this.add(x, y, 0);
    }

    public BlockVector3 add(int x, int y, int z) {
        return new BlockVector3(this.x + x, this.y + y, this.z + z);
    }

    public BlockVector3 add(BlockVector3 x) {
        return new BlockVector3(this.x + x.getX(), this.y + x.getY(), this.z + x.getZ());
    }

    public BlockVector3 subtract() {
        return this.subtract(0, 0, 0);
    }

    public BlockVector3 subtract(int x) {
        return this.subtract(x, 0, 0);
    }

    public BlockVector3 subtract(int x, int y) {
        return this.subtract(x, y, 0);
    }

    public BlockVector3 subtract(int x, int y, int z) {
        return this.add(-x, -y, -z);
    }

    public BlockVector3 subtract(BlockVector3 x) {
        return this.add(-x.getX(), -x.getY(), -x.getZ());
    }

    public BlockVector3 multiply(int number) {
        return new BlockVector3(this.x * number, this.y * number, this.z * number);
    }

    public BlockVector3 divide(int number) {
        return new BlockVector3(this.x / number, this.y / number, this.z / number);
    }

    public BlockVector3 getSide(BlockFace face) {
        return this.getSide(face, 1);
    }

    public BlockVector3 getSide(BlockFace face, int step) {
        return new BlockVector3(this.getX() + face.getXOffset() * step, this.getY() + face.getYOffset() * step, this.getZ() + face.getZOffset() * step);
    }

    public BlockVector3 up() {
        return up(1);
    }

    public BlockVector3 up(int step) {
        return getSide(BlockFace.UP, step);
    }

    public BlockVector3 down() {
        return down(1);
    }

    public BlockVector3 down(int step) {
        return getSide(BlockFace.DOWN, step);
    }

    public BlockVector3 north() {
        return north(1);
    }

    public BlockVector3 north(int step) {
        return getSide(BlockFace.NORTH, step);
    }

    public BlockVector3 south() {
        return south(1);
    }

    public BlockVector3 south(int step) {
        return getSide(BlockFace.SOUTH, step);
    }

    public BlockVector3 east() {
        return east(1);
    }

    public BlockVector3 east(int step) {
        return getSide(BlockFace.EAST, step);
    }

    public BlockVector3 west() {
        return west(1);
    }

    public BlockVector3 west(int step) {
        return getSide(BlockFace.WEST, step);
    }

    public double distance(Vector3 pos) {
        return Math.sqrt(this.distanceSquared(pos));
    }

    public double distance(BlockVector3 pos) {
        return Math.sqrt(this.distanceSquared(pos));
    }

    public double distanceSquared(Vector3 pos) {
        return distanceSquared(pos.x, pos.y, pos.z);
    }

    public double distanceSquared(BlockVector3 pos) {
        return distanceSquared(pos.x, pos.y, pos.z);
    }

    public double distanceSquared(double x, double y, double z) {
        return Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2) + Math.pow(this.z - z, 2);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getAxis(BlockFace.Axis axis) {
        switch (axis) {
            case X:
                return x;
            case Y:
                return y;
            default:
                return z;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;

        if (!(o instanceof BlockVector3)) return false;
        BlockVector3 that = (BlockVector3) o;

        return this.x == that.x &&
                this.y == that.y &&
                this.z == that.z;
    }

    @Override
    public final int hashCode() {
        return (x ^ (z << 12)) ^ (y << 24);
    }

    @Override
    public String toString() {
        return "BlockPosition(level=" + ",x=" + this.x + ",y=" + this.y + ",z=" + this.z + ")";
    }

    @Override
    public BlockVector3 clone() {
        try {
            return (BlockVector3) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Vector3 asVector3() {
        return new Vector3(this.x, this.y, this.z);
    }

    public Vector3f asVector3f() {
        return new Vector3f(this.x, this.y, this.z);
    }
}

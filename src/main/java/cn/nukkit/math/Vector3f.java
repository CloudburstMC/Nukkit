package cn.nukkit.math;

import cn.nukkit.level.BlockPosition;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Vector3f implements Cloneable {

    public double x;
    public double y;
    public double z;

    public Vector3f() {
        this(0, 0, 0);
    }

    public Vector3f(double x) {
        this(x, 0, 0);
    }

    public Vector3f(double x, double y) {
        this(x, y, 0);
    }

    public Vector3f(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public int getFloorX() {
        return (int) Math.floor(this.x);
    }

    public int getFloorY() {
        return (int) Math.floor(this.y);
    }

    public int getFloorZ() {
        return (int) Math.floor(this.z);
    }

    public int getChunkX() {
        return getFloorX() >> 4;
    }

    public int getChunkZ() {
        return getFloorZ() >> 4;
    }

    public double getRight() {
        return this.x;
    }

    public double getUp() {
        return this.y;
    }

    public double getForward() {
        return this.z;
    }

    public double getSouth() {
        return this.x;
    }

    public double getWest() {
        return this.z;
    }

    public Vector3f add(double x) {
        return this.add(x, 0, 0);
    }

    public Vector3f add(double x, double y) {
        return this.add(x, y, 0);
    }

    public Vector3f add(double x, double y, double z) {
        return new Vector3f(this.x + x, this.y + y, this.z + z);
    }

    public Vector3f add(Vector3f x) {
        return new Vector3f(this.x + x.getX(), this.y + x.getY(), this.z + x.getZ());
    }

    public Vector3f subtract() {
        return this.subtract(0, 0, 0);
    }

    public Vector3f subtract(double x) {
        return this.subtract(x, 0, 0);
    }

    public Vector3f subtract(double x, double y) {
        return this.subtract(x, y, 0);
    }

    public Vector3f subtract(double x, double y, double z) {
        return this.add(-x, -y, -z);
    }

    public Vector3f subtract(Vector3f x) {
        return this.add(-x.getX(), -x.getY(), -x.getZ());
    }

    public Vector3f multiply(double val) {
        return new Vector3f(this.x * val, this.y * val, this.z * val);
    }

    public Vector3f multiply(double x, double y, double z) {
        return new Vector3f(this.x * x, this.y * y, this.z * z);
    }

    public Vector3f divide(double number) {
        return new Vector3f(this.x / number, this.y / number, this.z / number);
    }

    public Vector3f ceil() {
        return new Vector3f((int) Math.ceil(this.x), (int) Math.ceil(this.y), (int) Math.ceil(this.z));
    }

    public Vector3f floor() {
        return new Vector3f(this.getFloorX(), this.getFloorY(), this.getFloorZ());
    }

    public Vector3f round() {
        return new Vector3f(Math.round(this.x), Math.round(this.y), Math.round(this.z));
    }

    public Vector3f abs() {
        return new Vector3f((int) Math.abs(this.x), (int) Math.abs(this.y), (int) Math.abs(this.z));
    }

    public Vector3f getSide(BlockFace face) {
        return this.getSide(face, 1);
    }

    public Vector3f getSide(BlockFace face, int step) {
        return new Vector3f(this.getX() + face.getXOffset() * step, this.getY() + face.getYOffset() * step, this.getZ() + face.getZOffset() * step);
    }

    public Vector3f up() {
        return up(1);
    }

    public Vector3f up(int step) {
        return getSide(BlockFace.UP, step);
    }

    public Vector3f down() {
        return down(1);
    }

    public Vector3f down(int step) {
        return getSide(BlockFace.DOWN, step);
    }

    public Vector3f north() {
        return north(1);
    }

    public Vector3f north(int step) {
        return getSide(BlockFace.NORTH, step);
    }

    public Vector3f south() {
        return south(1);
    }

    public Vector3f south(int step) {
        return getSide(BlockFace.SOUTH, step);
    }

    public Vector3f east() {
        return east(1);
    }

    public Vector3f east(int step) {
        return getSide(BlockFace.EAST, step);
    }

    public Vector3f west() {
        return west(1);
    }

    public Vector3f west(int step) {
        return getSide(BlockFace.WEST, step);
    }

    public double distance(Vector3i pos) {
        return Math.sqrt(this.distanceSquared(pos));
    }

    public double distance(Vector3f pos) {
        return Math.sqrt(this.distanceSquared(pos));
    }

    public double distanceSquared(Vector3f pos) {
        return Math.pow(this.x - pos.x, 2) + Math.pow(this.y - pos.y, 2) + Math.pow(this.z - pos.z, 2);
    }

    public double distanceSquared(Vector3i pos) {
        return Math.pow(this.x - pos.x, 2) + Math.pow(this.y - pos.y, 2) + Math.pow(this.z - pos.z, 2);
    }

    public double maxPlainDistance() {
        return this.maxPlainDistance(0, 0);
    }

    public double maxPlainDistance(double x) {
        return this.maxPlainDistance(x, 0);
    }

    public double maxPlainDistance(double x, double z) {
        return Math.max(Math.abs(this.x - x), Math.abs(this.z - z));
    }

    public double maxPlainDistance(Vector2f vector) {
        return this.maxPlainDistance(vector.x, vector.y);
    }

    public double maxPlainDistance(Vector3f x) {
        return this.maxPlainDistance(x.x, x.z);
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public Vector3f normalize() {
        double len = this.lengthSquared();
        if (len > 0) {
            return this.divide(Math.sqrt(len));
        }
        return new Vector3f(0, 0, 0);
    }

    public double dot(Vector3f v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public Vector3f cross(Vector3f v) {
        return new Vector3f(
                this.y * v.z - this.z * v.y,
                this.z * v.x - this.x * v.z,
                this.x * v.y - this.y * v.x
        );
    }

    /**
     * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     *
     * @param v vector
     * @param x x value
     * @return intermediate vector
     */
    public Vector3f getIntermediateWithXValue(Vector3f v, double x) {
        double xDiff = v.x - this.x;
        double yDiff = v.y - this.y;
        double zDiff = v.z - this.z;
        if (xDiff * xDiff < 0.0000001) {
            return null;
        }
        double f = (x - this.x) / xDiff;
        if (f < 0 || f > 1) {
            return null;
        } else {
            return new Vector3f(this.x + xDiff * f, this.y + yDiff * f, this.z + zDiff * f);
        }
    }

    /**
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     *
     * @param v vector
     * @param y y value
     * @return intermediate vector
     */
    public Vector3f getIntermediateWithYValue(Vector3f v, double y) {
        double xDiff = v.x - this.x;
        double yDiff = v.y - this.y;
        double zDiff = v.z - this.z;
        if (yDiff * yDiff < 0.0000001) {
            return null;
        }
        double f = (y - this.y) / yDiff;
        if (f < 0 || f > 1) {
            return null;
        } else {
            return new Vector3f(this.x + xDiff * f, this.y + yDiff * f, this.z + zDiff * f);
        }
    }

    /**
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     *
     * @param v vector
     * @param z z value
     * @return intermediate vector
     */
    public Vector3f getIntermediateWithZValue(Vector3f v, double z) {
        double xDiff = v.x - this.x;
        double yDiff = v.y - this.y;
        double zDiff = v.z - this.z;
        if (zDiff * zDiff < 0.0000001) {
            return null;
        }
        double f = (z - this.z) / zDiff;
        if (f < 0 || f > 1) {
            return null;
        } else {
            return new Vector3f(this.x + xDiff * f, this.y + yDiff * f, this.z + zDiff * f);
        }
    }

    public Vector3f setComponents(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override
    public String toString() {
        return "Vector3(x=" + this.x + ", y=" + this.y + ", z=" + this.z + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector3f)) {
            return false;
        }

        Vector3f other = (Vector3f) obj;

        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    @Override
    public int hashCode() {
        return ((int) x ^ ((int) z << 12)) ^ ((int) y << 24);
    }

    public int rawHashCode() {
        return super.hashCode();
    }

    @Override
    public Vector3f clone() {
        try {
            return (Vector3f) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Vector3f asVector3f() {
        return new Vector3f((float) this.x, (float) this.y, (float) this.z);
    }

    public Vector3i asVector3i() {
        return new Vector3i(this.getFloorX(), this.getFloorY(), this.getFloorZ());
    }

    public BlockPosition asBlockPosition() {
        return new BlockPosition(this.getFloorX(), this.getFloorY(), this.getFloorZ());
    }
}

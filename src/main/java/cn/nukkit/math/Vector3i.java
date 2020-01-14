package cn.nukkit.math;

public class Vector3i implements Cloneable {
    public int x;
    public int y;
    public int z;

    public Vector3i() {
        this(0, 0, 0);
    }

    public Vector3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3i setComponents(int x, int y, int z) {
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

    public int getChunkX() {
        return this.x >> 4;
    }

    public int getChunkZ() {
        return this.z >> 4;
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

    public Vector3i add(int x) {
        return this.add(x, 0, 0);
    }

    public Vector3i add(int x, int y) {
        return this.add(x, y, 0);
    }

    public Vector3i add(int x, int y, int z) {
        return new Vector3i(this.x + x, this.y + y, this.z + z);
    }

    public Vector3i add(Vector3i x) {
        return new Vector3i(this.x + x.getX(), this.y + x.getY(), this.z + x.getZ());
    }

    public Vector3i subtract() {
        return this.subtract(0, 0, 0);
    }

    public Vector3i subtract(int x) {
        return this.subtract(x, 0, 0);
    }

    public Vector3i subtract(int x, int y) {
        return this.subtract(x, y, 0);
    }

    public Vector3i subtract(int x, int y, int z) {
        return this.add(-x, -y, -z);
    }

    public Vector3i subtract(Vector3i x) {
        return this.add(-x.getX(), -x.getY(), -x.getZ());
    }

    public Vector3i multiply(int number) {
        return new Vector3i(this.x * number, this.y * number, this.z * number);
    }

    public Vector3i divide(int number) {
        return new Vector3i(this.x / number, this.y / number, this.z / number);
    }

    public Vector3i getSide(BlockFace face) {
        return this.getSide(face, 1);
    }

    public Vector3i getSide(BlockFace face, int step) {
        return new Vector3i(this.getX() + face.getXOffset() * step, this.getY() + face.getYOffset() * step, this.getZ() + face.getZOffset() * step);
    }

    public Vector3i up() {
        return up(1);
    }

    public Vector3i up(int step) {
        return getSide(BlockFace.UP, step);
    }

    public Vector3i down() {
        return down(1);
    }

    public Vector3i down(int step) {
        return getSide(BlockFace.DOWN, step);
    }

    public Vector3i north() {
        return north(1);
    }

    public Vector3i north(int step) {
        return getSide(BlockFace.NORTH, step);
    }

    public Vector3i south() {
        return south(1);
    }

    public Vector3i south(int step) {
        return getSide(BlockFace.SOUTH, step);
    }

    public Vector3i east() {
        return east(1);
    }

    public Vector3i east(int step) {
        return getSide(BlockFace.EAST, step);
    }

    public Vector3i west() {
        return west(1);
    }

    public Vector3i west(int step) {
        return getSide(BlockFace.WEST, step);
    }

    public double distance(Vector3f pos) {
        return Math.sqrt(this.distanceSquared(pos));
    }

    public double distance(Vector3i pos) {
        return Math.sqrt(this.distanceSquared(pos));
    }

    public double distanceSquared(Vector3f pos) {
        return distanceSquared(pos.x, pos.y, pos.z);
    }

    public double distanceSquared(Vector3i pos) {
        return distanceSquared(pos.x, pos.y, pos.z);
    }

    public double distanceSquared(double x, double y, double z) {
        return Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2) + Math.pow(this.z - z, 2);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;

        if (!(o instanceof Vector3i)) return false;
        Vector3i that = (Vector3i) o;

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
        return "Vector3i(x=" + this.x + ", y=" + this.y + ", z=" + this.z + ")";
    }

    @Override
    public Vector3i clone() {
        try {
            return (Vector3i) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public Vector3f asVector3f() {
        return new Vector3f(this.x, this.y, this.z);
    }
}

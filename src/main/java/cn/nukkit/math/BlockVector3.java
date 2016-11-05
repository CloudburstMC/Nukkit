package cn.nukkit.math;

public class BlockVector3 implements Cloneable {
    public static final int SIDE_DOWN = 0;
    public static final int SIDE_UP = 1;
    public static final int SIDE_NORTH = 2;
    public static final int SIDE_SOUTH = 3;
    public static final int SIDE_WEST = 4;
    public static final int SIDE_EAST = 5;

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

    public BlockVector3 getSide(int side) {
        return this.getSide(side, 1);
    }

    public BlockVector3 getSide(int side, int step) {
        switch (side) {
            case BlockVector3.SIDE_DOWN:
                return new BlockVector3(this.x, this.y - step, this.z);
            case BlockVector3.SIDE_UP:
                return new BlockVector3(this.x, this.y + step, this.z);
            case BlockVector3.SIDE_NORTH:
                return new BlockVector3(this.x, this.y, this.z - step);
            case BlockVector3.SIDE_SOUTH:
                return new BlockVector3(this.x, this.y, this.z + step);
            case BlockVector3.SIDE_WEST:
                return new BlockVector3(this.x - step, this.y, this.z);
            case BlockVector3.SIDE_EAST:
                return new BlockVector3(this.x + step, this.y, this.z);
            default:
                return this;
        }
    }

    public static int getOppositeSide(int side) {
        switch (side) {
            case BlockVector3.SIDE_DOWN:
                return BlockVector3.SIDE_UP;
            case BlockVector3.SIDE_UP:
                return BlockVector3.SIDE_DOWN;
            case BlockVector3.SIDE_NORTH:
                return BlockVector3.SIDE_SOUTH;
            case BlockVector3.SIDE_SOUTH:
                return BlockVector3.SIDE_NORTH;
            case BlockVector3.SIDE_WEST:
                return BlockVector3.SIDE_EAST;
            case BlockVector3.SIDE_EAST:
                return BlockVector3.SIDE_WEST;
            default:
                return -1;
        }
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

    @Override
    public boolean equals(Object ob) {
        if (ob == null) return false;
        if (ob == this) return true;

        if (!(ob instanceof BlockVector3)) return false;

        return this.x == ((BlockVector3) ob).x && this.z == ((BlockVector3) ob).z;
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
}

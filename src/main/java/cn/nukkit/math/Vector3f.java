package cn.nukkit.math;

public class Vector3f implements Cloneable {
    public static final int SIDE_DOWN = 0;
    public static final int SIDE_UP = 1;
    public static final int SIDE_NORTH = 2;
    public static final int SIDE_SOUTH = 3;
    public static final int SIDE_WEST = 4;
    public static final int SIDE_EAST = 5;

    public float x;
    public float y;
    public float z;

    public Vector3f() {
        this(0, 0, 0);
    }

    public Vector3f(float x) {
        this(x, 0, 0);
    }

    public Vector3f(float x, float y) {
        this(x, y, 0);
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public int getFloorX() {
        return NukkitMath.floorFloat(this.x);
    }

    public int getFloorY() {
        return NukkitMath.floorFloat(this.y);
    }

    public int getFloorZ() {
        return NukkitMath.floorFloat(this.z);
    }

    public float getRight() {
        return this.x;
    }

    public float getUp() {
        return this.y;
    }

    public float getForward() {
        return this.z;
    }

    public float getSouth() {
        return this.x;
    }

    public float getWest() {
        return this.z;
    }

    public Vector3f add(float x) {
        return this.add(x, 0, 0);
    }

    public Vector3f add(float x, float y) {
        return this.add(x, y, 0);
    }

    public Vector3f add(float x, float y, float z) {
        return new Vector3f(this.x + x, this.y + y, this.z + z);
    }

    public Vector3f add(Vector3f x) {
        return new Vector3f(this.x + x.getX(), this.y + x.getY(), this.z + x.getZ());
    }

    public Vector3f subtract() {
        return this.subtract(0, 0, 0);
    }

    public Vector3f subtract(float x) {
        return this.subtract(x, 0, 0);
    }

    public Vector3f subtract(float x, float y) {
        return this.subtract(x, y, 0);
    }

    public Vector3f subtract(float x, float y, float z) {
        return this.add(-x, -y, -z);
    }

    public Vector3f subtract(Vector3f x) {
        return this.add(-x.getX(), -x.getY(), -x.getZ());
    }

    public Vector3f multiply(float number) {
        return new Vector3f(this.x * number, this.y * number, this.z * number);
    }

    public Vector3f divide(float number) {
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

    public Vector3f getSide(int side) {
        return this.getSide(side, 1);
    }

    public Vector3f getSide(int side, int step) {
        switch (side) {
            case Vector3f.SIDE_DOWN:
                return new Vector3f(this.x, this.y - step, this.z);
            case Vector3f.SIDE_UP:
                return new Vector3f(this.x, this.y + step, this.z);
            case Vector3f.SIDE_NORTH:
                return new Vector3f(this.x, this.y, this.z - step);
            case Vector3f.SIDE_SOUTH:
                return new Vector3f(this.x, this.y, this.z + step);
            case Vector3f.SIDE_WEST:
                return new Vector3f(this.x - step, this.y, this.z);
            case Vector3f.SIDE_EAST:
                return new Vector3f(this.x + step, this.y, this.z);
            default:
                return this;
        }
    }

    public static int getOppositeSide(int side) {
        switch (side) {
            case Vector3f.SIDE_DOWN:
                return Vector3f.SIDE_UP;
            case Vector3f.SIDE_UP:
                return Vector3f.SIDE_DOWN;
            case Vector3f.SIDE_NORTH:
                return Vector3f.SIDE_SOUTH;
            case Vector3f.SIDE_SOUTH:
                return Vector3f.SIDE_NORTH;
            case Vector3f.SIDE_WEST:
                return Vector3f.SIDE_EAST;
            case Vector3f.SIDE_EAST:
                return Vector3f.SIDE_WEST;
            default:
                return -1;
        }
    }

    public double distance(Vector3f pos) {
        return Math.sqrt(this.distanceSquared(pos));
    }

    public double distanceSquared(Vector3f pos) {
        return Math.pow(this.x - pos.x, 2) + Math.pow(this.y - pos.y, 2) + Math.pow(this.z - pos.z, 2);
    }

    public float maxPlainDistance() {
        return this.maxPlainDistance(0, 0);
    }

    public float maxPlainDistance(float x) {
        return this.maxPlainDistance(x, 0);
    }

    public float maxPlainDistance(float x, float z) {
        return Math.max(Math.abs(this.x - x), Math.abs(this.z - z));
    }

    public float maxPlainDistance(Vector2f vector) {
        return this.maxPlainDistance(vector.x, vector.y);
    }

    public float maxPlainDistance(Vector3f x) {
        return this.maxPlainDistance(x.x, x.z);
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public float lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public Vector3f normalize() {
        float len = this.lengthSquared();
        if (len > 0) {
            return this.divide((float) Math.sqrt(len));
        }
        return new Vector3f(0, 0, 0);
    }

    public float dot(Vector3f v) {
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
     */
    public Vector3f getIntermediateWithXValue(Vector3f v, float x) {
        float xDiff = v.x - this.x;
        float yDiff = v.y - this.y;
        float zDiff = v.z - this.z;
        if (xDiff * xDiff < 0.0000001) {
            return null;
        }
        float f = (x - this.x) / xDiff;
        if (f < 0 || f > 1) {
            return null;
        } else {
            return new Vector3f(this.x + xDiff * f, this.y + yDiff * f, this.z + zDiff * f);
        }
    }

    /**
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vector3f getIntermediateWithYValue(Vector3f v, float y) {
        float xDiff = v.x - this.x;
        float yDiff = v.y - this.y;
        float zDiff = v.z - this.z;
        if (yDiff * yDiff < 0.0000001) {
            return null;
        }
        float f = (y - this.y) / yDiff;
        if (f < 0 || f > 1) {
            return null;
        } else {
            return new Vector3f(this.x + xDiff * f, this.y + yDiff * f, this.z + zDiff * f);
        }
    }

    /**
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vector3f getIntermediateWithZValue(Vector3f v, float z) {
        float xDiff = v.x - this.x;
        float yDiff = v.y - this.y;
        float zDiff = v.z - this.z;
        if (zDiff * zDiff < 0.0000001) {
            return null;
        }
        float f = (z - this.z) / zDiff;
        if (f < 0 || f > 1) {
            return null;
        } else {
            return new Vector3f(this.x + xDiff * f, this.y + yDiff * f, this.z + zDiff * f);
        }
    }

    public Vector3f setComponents(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override
    public String toString() {
        return "Vector3(x=" + this.x + ",y=" + this.y + ",z=" + this.z + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector3f)) {
            return false;
        }

        Vector3f other = (Vector3f) obj;

        return this.x == other.x && this.y == other.y && this.z == other.z;
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

    public Vector3 asVector3() {
        return new Vector3(this.x, this.y, this.z);
    }

    public BlockVector3 asBlockVector3() {
        return new BlockVector3(getFloorX(), getFloorY(), getFloorZ());
    }
}
package cn.nukkit.server.math;

public class Vector3 implements Cloneable {

    public static final int SIDE_DOWN = 0;
    public static final int SIDE_UP = 1;
    public static final int SIDE_NORTH = 2;
    public static final int SIDE_SOUTH = 3;
    public static final int SIDE_WEST = 4;
    public static final int SIDE_EAST = 5;

    public float x;
    public float y;
    public float z;

    public Vector3() {
        this(0, 0, 0);
    }

    public Vector3(float x) {
        this(x, 0, 0);
    }

    public Vector3(float x, float y) {
        this(x, y, 0);
    }

    public Vector3(float x, float y, float z) {
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

    public Vector3 add(float x) {
        return this.add(x, 0, 0);
    }

    public Vector3 add(float x, float y) {
        return this.add(x, y, 0);
    }

    public Vector3 add(float x, float y, float z) {
        return new Vector3(this.x + x, this.y + y, this.z + z);
    }

    public Vector3 add(Vector3 x) {
        return new Vector3(this.x + x.getX(), this.y + x.getY(), this.z + x.getZ());
    }

    public Vector3 subtract() {
        return this.subtract(0, 0, 0);
    }

    public Vector3 subtract(float x) {
        return this.subtract(x, 0, 0);
    }

    public Vector3 subtract(float x, float y) {
        return this.subtract(x, y, 0);
    }

    public Vector3 subtract(float x, float y, float z) {
        return this.add(-x, -y, -z);
    }

    public Vector3 subtract(Vector3 x) {
        return this.add(-x.getX(), -x.getY(), -x.getZ());
    }

    public Vector3 multiply(float number) {
        return new Vector3(this.x * number, this.y * number, this.z * number);
    }

    public Vector3 divide(float number) {
        return new Vector3(this.x / number, this.y / number, this.z / number);
    }

    public Vector3 ceil() {
        return new Vector3((int) Math.ceil(this.x), (int) Math.ceil(this.y), (int) Math.ceil(this.z));
    }

    public Vector3 floor() {
        return new Vector3(this.getFloorX(), this.getFloorY(), this.getFloorZ());
    }

    public Vector3 round() {
        return new Vector3(Math.round(this.x), Math.round(this.y), Math.round(this.z));
    }

    public Vector3 abs() {
        return new Vector3((int) Math.abs(this.x), (int) Math.abs(this.y), (int) Math.abs(this.z));
    }

    public Vector3 getSide(BlockFace face) {
        return this.getSide(face, 1);
    }

    public Vector3 getSide(BlockFace face, int step) {
        return new Vector3(this.getX() + face.getXOffset() * step, this.getY() + face.getYOffset() * step, this.getZ() + face.getZOffset() * step);
    }

    public Vector3 up() {
        return up(1);
    }

    public Vector3 up(int step) {
        return getSide(BlockFace.UP, step);
    }

    public Vector3 down() {
        return down(1);
    }

    public Vector3 down(int step) {
        return getSide(BlockFace.DOWN, step);
    }

    public Vector3 north() {
        return north(1);
    }

    public Vector3 north(int step) {
        return getSide(BlockFace.NORTH, step);
    }

    public Vector3 south() {
        return south(1);
    }

    public Vector3 south(int step) {
        return getSide(BlockFace.SOUTH, step);
    }

    public Vector3 east() {
        return east(1);
    }

    public Vector3 east(int step) {
        return getSide(BlockFace.EAST, step);
    }

    public Vector3 west() {
        return west(1);
    }

    public Vector3 west(int step) {
        return getSide(BlockFace.WEST, step);
    }

    public double distance(Vector3 pos) {
        return Math.sqrt(this.distanceSquared(pos));
    }

    public double distanceSquared(Vector3 pos) {
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

    public float maxPlainDistance(Vector3 x) {
        return this.maxPlainDistance(x.x, x.z);
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public float lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public Vector3 normalize() {
        float len = this.lengthSquared();
        if (len > 0) {
            return this.divide((float) Math.sqrt(len));
        }
        return new Vector3(0, 0, 0);
    }

    public float dot(Vector3 v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public Vector3 cross(Vector3 v) {
        return new Vector3(
                this.y * v.z - this.z * v.y,
                this.z * v.x - this.x * v.z,
                this.x * v.y - this.y * v.x
        );
    }

    /**
     * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vector3 getIntermediateWithXValue(Vector3 v, float x) {
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
            return new Vector3(this.x + xDiff * f, this.y + yDiff * f, this.z + zDiff * f);
        }
    }

    /**
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vector3 getIntermediateWithYValue(Vector3 v, float y) {
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
            return new Vector3(this.x + xDiff * f, this.y + yDiff * f, this.z + zDiff * f);
        }
    }

    /**
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vector3 getIntermediateWithZValue(Vector3 v, float z) {
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
            return new Vector3(this.x + xDiff * f, this.y + yDiff * f, this.z + zDiff * f);
        }
    }

    public Vector3 setComponents(float x, float y, float z) {
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
        if (!(obj instanceof Vector3)) {
            return false;
        }

        Vector3 other = (Vector3) obj;

        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    public int rawHashCode() {
        return super.hashCode();
    }

    @Override
    public Vector3 clone() {
        try {
            return (Vector3) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
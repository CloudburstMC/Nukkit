package cn.nukkit.math;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class Vector2f {
    public final float x;
    public final float y;

    public Vector2f() {
        this(0, 0);
    }

    public Vector2f(float x) {
        this(x, 0);
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public int getFloorX() {
        return NukkitMath.floorFloat(this.x);
    }

    public int getFloorY() {
        return NukkitMath.floorFloat(this.y);
    }

    public Vector2f add(float x) {
        return this.add(x, 0);
    }

    public Vector2f add(float x, float y) {
        return new Vector2f(this.x + x, this.y + y);
    }

    public Vector2f add(Vector2f x) {
        return this.add(x.getX(), x.getY());
    }

    public Vector2f subtract(float x) {
        return this.subtract(x, 0);
    }

    public Vector2f subtract(float x, float y) {
        return this.add(-x, -y);
    }

    public Vector2f subtract(Vector2f x) {
        return this.add(-x.getX(), -x.getY());
    }

    public Vector2f ceil() {
        return new Vector2f((int) (this.x + 1), (int) (this.y + 1));
    }

    public Vector2f floor() {
        return new Vector2f(this.getFloorX(), this.getFloorY());
    }

    public Vector2f round() {
        return new Vector2f(Math.round(this.x), Math.round(this.y));
    }

    public Vector2f abs() {
        return new Vector2f(Math.abs(this.x), Math.abs(this.y));
    }

    public Vector2f multiply(float number) {
        return new Vector2f(this.x * number, this.y * number);
    }

    public Vector2f divide(float number) {
        return new Vector2f(this.x / number, this.y / number);
    }

    public double distance(float x) {
        return this.distance(x, 0);
    }

    public double distance(float x, float y) {
        return Math.sqrt(this.distanceSquared(x, y));
    }

    public double distance(Vector2f vector) {
        return Math.sqrt(this.distanceSquared(vector.getX(), vector.getY()));
    }

    public double distanceSquared(float x) {
        return this.distanceSquared(x, 0);
    }

    public double distanceSquared(float x, float y) {
        return Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2);
    }

    public double distanceSquared(Vector2f vector) {
        return this.distanceSquared(vector.getX(), vector.getY());
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public float lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public Vector2f normalize() {
        float len = this.lengthSquared();
        if (len != 0) {
            return this.divide((float) Math.sqrt(len));
        }
        return new Vector2f(0, 0);
    }

    public float dot(Vector2f v) {
        return this.x * v.x + this.y * v.y;
    }

    @Override
    public String toString() {
        return "Vector2(x=" + this.x + ",y=" + this.y + ")";
    }

}

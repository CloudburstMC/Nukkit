package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.level.chunk.Chunk;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Location {
    private final Vector3f position;
    private final float yaw;
    private final float pitch;
    private final Level level;

    private Location(Vector3f position, float yaw, float pitch, Level level) {
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
        this.level = level;
    }

    public static Location from(Level level) {
        return from(Vector3f.ZERO, level);
    }

    public static Location from(Vector3i position, Level level) {
        return from(position.getX(), position.getY(), position.getZ(), 0f, 0f, level);
    }

    public static Location from(float x, float y, float z, Level level) {
        return from(x, y, z, 0, 0, level);
    }

    public static Location from(Vector3f position, Level level) {
        return from(position, 0f, 0f, level);
    }

    public static Location from(Vector3f position, float yaw, float pitch, Level level) {
        checkNotNull(position, "position");
        checkNotNull(level, "level");
        return new Location(position, yaw, pitch, level);
    }

    public static Location from(float x, float y, float z, float yaw, float pitch, Level level) {
        checkNotNull(level, "level");
        return new Location(Vector3f.from(x, y, z), yaw, pitch, level);
    }

    public float getX() {
        return this.position.getX();
    }

    public float getY() {
        return this.position.getX();
    }

    public float getZ() {
        return this.position.getX();
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public Level getLevel() {
        return level;
    }

    public Chunk getChunk() {
        return level.getChunk(this.getChunkX(), this.getChunkZ());
    }

    public Block getBlock() {
        return level.getBlock(this.position);
    }

    public Location add(double x, double y, double z) {
        return Location.from(Vector3f.from(x, y, z), this.yaw, this.pitch, this.level);
    }

    public Location add(float x, float y, float z) {
        return Location.from(Vector3f.from(x, y, z), this.yaw, this.pitch, this.level);
    }

    public int getFloorX() {
        return this.position.getFloorX();
    }

    public int getFloorY() {
        return this.position.getFloorY();
    }

    public int getFloorZ() {
        return this.position.getFloorZ();
    }

    public int getChunkX() {
        return this.getFloorX() >> 4;
    }

    public int getChunkY() {
        return this.getFloorY() >> 4;
    }

    public int getChunkZ() {
        return this.getFloorZ() >> 4;
    }
}

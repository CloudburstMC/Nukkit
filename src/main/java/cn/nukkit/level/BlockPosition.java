package cn.nukkit.level;

import cn.nukkit.block.Block;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.math.Vector3i;
import cn.nukkit.utils.LevelException;

public class BlockPosition extends Vector3i {
    public Level level;
    public int layer;

    public BlockPosition() {
        super();
    }

    public BlockPosition(int x, int y, int z) {
        super(x, y, z);
    }

    public BlockPosition(int x, int y, int z, Level level) {
        this(x, y, z, level, 0);
    }

    public BlockPosition(int x, int y, int z, Level level, int layer) {
        super(x, y, z);
        this.level = level;
        this.layer = layer;
    }

    public static BlockPosition from(Vector3i vector3i) {
        return from(vector3i, null);
    }

    public static BlockPosition from(Vector3i vector3i, Level level) {
        return from(vector3i, level, 0);
    }

    public static BlockPosition from(Vector3i vector3i, Level level, int layer) {
        return new BlockPosition(vector3i.x, vector3i.y, vector3i.z, level, layer);
    }

    public boolean isValid() {
        return this.level != null && layer >= 0 && layer < 2;
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
        return new Position(this.x + x, this.y + y, this.z + z, level);
    }

    @Override
    public Position add(Vector3f x) {
        return new Position(this.x + x.x, this.y + x.y, this.z + x.z, level);
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
    public Position subtract(Vector3f val) {
        return this.add(-val.x, -val.y, -val.z);
    }

    @Override
    public BlockPosition add(int x) {
        return this.add(x, 0, 0);
    }

    @Override
    public BlockPosition add(int x, int y) {
        return this.add(x, y, 0);
    }

    @Override
    public BlockPosition add(int x, int y, int z) {
        return new BlockPosition(this.x + x, this.y + y, this.z + z, level, layer);
    }

    @Override
    public BlockPosition add(Vector3i val) {
        return new BlockPosition(this.x + val.z, this.y + val.y, this.z + val.z, level, layer);
    }

    @Override
    public BlockPosition subtract() {
        return this.subtract(0, 0, 0);
    }

    @Override
    public BlockPosition subtract(int x) {
        return this.subtract(x, 0, 0);
    }

    @Override
    public BlockPosition subtract(int x, int y) {
        return this.subtract(x, y, 0);
    }

    @Override
    public BlockPosition subtract(int x, int y, int z) {
        return this.add(-x, -y, -z);
    }

    @Override
    public BlockPosition subtract(Vector3i val) {
        return this.add(-val.x, -val.y, -val.z);
    }

    @Override
    public BlockPosition multiply(int number) {
        return new BlockPosition(this.x * number, this.y * number, this.z * number, level, layer);
    }

    @Override
    public BlockPosition divide(int number) {
        return new BlockPosition(this.x / number, this.y / number, this.z / number, level, layer);
    }

    @Override
    public BlockPosition getSide(BlockFace face) {
        return this.getSide(face, 1);
    }

    @Override
    public BlockPosition getSide(BlockFace face, int step) {
        return new BlockPosition(this.x + face.getXOffset() * step, this.y + face.getYOffset() * step,
                this.z + face.getZOffset() * step, level, layer);
    }

    @Override
    public BlockPosition up() {
        return up(1);
    }

    @Override
    public BlockPosition up(int step) {
        return getSide(BlockFace.UP, step);
    }

    @Override
    public BlockPosition down() {
        return down(1);
    }

    @Override
    public BlockPosition down(int step) {
        return getSide(BlockFace.DOWN, step);
    }

    @Override
    public BlockPosition north() {
        return north(1);
    }

    @Override
    public BlockPosition north(int step) {
        return getSide(BlockFace.NORTH, step);
    }

    @Override
    public BlockPosition south() {
        return south(1);
    }

    @Override
    public BlockPosition south(int step) {
        return getSide(BlockFace.SOUTH, step);
    }

    @Override
    public BlockPosition east() {
        return east(1);
    }

    @Override
    public BlockPosition east(int step) {
        return getSide(BlockFace.EAST, step);
    }

    @Override
    public BlockPosition west() {
        return west(1);
    }

    @Override
    public BlockPosition west(int step) {
        return getSide(BlockFace.WEST, step);
    }

    public Level getLevel() {
        return level;
    }

    public BlockPosition setLevel(Level level) {
        this.level = level;
        return this;
    }

    public int getLayer() {
        return layer;
    }

    public BlockPosition setLayer(int layer) {
        this.layer = layer;
        return this;
    }

    public Block getBlock() {
        if (this.level != null) return this.level.getBlock(this);
        else throw new LevelException("Undefined Level reference");
    }

    public Chunk getChunk() {
        return level != null ? level.getChunk(getChunkX(), getChunkZ()) : null;
    }

    @Override
    public String toString() {
        return "BlockPosition(level=" + (this.level != null ? this.level.getName() : "null") +
                ", x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", layer=" + this.layer + ")";
    }

    @Override
    public BlockPosition clone() {
        return (BlockPosition) super.clone();
    }

    public Vector3f asVector3f() {
        return new Vector3f(this.x, this.y, this.z);
    }

    public Vector3i asVector3i() {
        return new Vector3i(this.x, this.y, this.z);
    }
}

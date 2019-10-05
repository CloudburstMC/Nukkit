package cn.nukkit.math;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Iterator;

public class BlockRayTrace implements Iterable<BlockVector3> {
    private final Vector3 start;
    private final Vector3 end;
    private final Vector3 direction;

    private final double stepX;
    private final double stepY;
    private final double stepZ;

    private final double deltaX;
    private final double deltaY;
    private final double deltaZ;

    private BlockRayTrace(Vector3 start, Vector3 end, Vector3 direction) {
        this.start = start;
        this.end = end;
        Preconditions.checkArgument(direction.lengthSquared() > 0, "Invalid direction vector");
        this.direction = direction;

        this.stepX = Double.compare(direction.x, 0);
        this.stepY = Double.compare(direction.y, 0);
        this.stepZ = Double.compare(direction.z, 0);

        this.deltaX = BlockRayTrace.this.direction.x == 0 ? 0 : BlockRayTrace.this.stepX / BlockRayTrace.this.direction.x;
        this.deltaY = BlockRayTrace.this.direction.y == 0 ? 0 : BlockRayTrace.this.stepY / BlockRayTrace.this.direction.y;
        this.deltaZ = BlockRayTrace.this.direction.z == 0 ? 0 : BlockRayTrace.this.stepZ / BlockRayTrace.this.direction.z;
    }

    public static BlockRayTrace of(Vector3 start, Vector3 direction, double distance) {
        return new BlockRayTrace(start, start.add(direction.multiply(distance)), direction);
    }

    public static BlockRayTrace of(Vector3 start, Vector3 end) {
        return new BlockRayTrace(start, end, end.subtract(start).normalize());
    }

    private static double rayTraceDistanceToBoundary(double s, double ds) {
        if (ds == 0) {
            return Double.POSITIVE_INFINITY;
        }
        if (ds < 0) {
            s = -s;
            ds = -ds;
            if (Math.floor(s) == s) {
                return 0;
            }
        }

        return (1 - (s - Math.floor(s))) / ds;
    }

    public Vector3 getStart() {
        return start;
    }

    public Vector3 getEnd() {
        return end;
    }

    public Vector3 getDirection() {
        return direction;
    }

    public double distance() {
        return this.start.distance(this.end);
    }

    @Nonnull
    @Override
    public Iterator<BlockVector3> iterator() {
        return new BlockRayTraceIterator();
    }

    private class BlockRayTraceIterator implements Iterator<BlockVector3> {
        private Vector3 currentBlock;

        private double maxX;
        private double maxY;
        private double maxZ;

        public BlockRayTraceIterator() {
            this.maxX = rayTraceDistanceToBoundary(BlockRayTrace.this.start.x, BlockRayTrace.this.direction.x);
            this.maxY = rayTraceDistanceToBoundary(BlockRayTrace.this.start.y, BlockRayTrace.this.direction.y);
            this.maxZ = rayTraceDistanceToBoundary(BlockRayTrace.this.start.z, BlockRayTrace.this.direction.z);
        }

        @Override
        public boolean hasNext() {
            if (currentBlock == null) {
                return true;
            } else if (this.maxX < this.maxY && this.maxX < this.maxZ) {
                return !(this.maxX > BlockRayTrace.this.distance());
            } else if (this.maxY < this.maxZ) {
                return !(this.maxY > BlockRayTrace.this.distance());
            } else {
                return !(this.maxZ > BlockRayTrace.this.distance());
            }
        }

        @Override
        public BlockVector3 next() {
            if (currentBlock == null) {
                this.currentBlock = BlockRayTrace.this.start.floor();
                return currentBlock.asBlockVector3();
            }

            if (this.maxX < this.maxY && this.maxX < this.maxZ) {
                if (this.maxX > BlockRayTrace.this.distance()) {
                    return null;
                }
                this.currentBlock.x += BlockRayTrace.this.stepX;
                this.maxX += BlockRayTrace.this.deltaX;
            } else if (this.maxY < this.maxZ) {
                if (this.maxY > BlockRayTrace.this.distance()) {
                    return null;
                }
                this.currentBlock.y += BlockRayTrace.this.stepY;
                this.maxY += BlockRayTrace.this.deltaY;
            } else {
                if (this.maxZ > BlockRayTrace.this.distance()) {
                    return null;
                }
                this.currentBlock.z += BlockRayTrace.this.stepZ;
                this.maxZ += BlockRayTrace.this.deltaZ;
            }

            return currentBlock.asBlockVector3();
        }
    }
}

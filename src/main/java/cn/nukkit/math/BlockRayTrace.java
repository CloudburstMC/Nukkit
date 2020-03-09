package cn.nukkit.math;

import com.google.common.base.Preconditions;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;

import javax.annotation.Nonnull;
import java.util.Iterator;

public class BlockRayTrace implements Iterable<Vector3i> {
    private final Vector3f start;
    private final Vector3f end;
    private final Vector3f direction;

    private final double stepX;
    private final double stepY;
    private final double stepZ;

    private final double deltaX;
    private final double deltaY;
    private final double deltaZ;

    private BlockRayTrace(Vector3f start, Vector3f end, Vector3f direction) {
        this.start = start;
        this.end = end;
        Preconditions.checkArgument(direction.lengthSquared() > 0, "Invalid direction vector");
        this.direction = direction;

        this.stepX = Double.compare(direction.getX(), 0);
        this.stepY = Double.compare(direction.getY(), 0);
        this.stepZ = Double.compare(direction.getZ(), 0);

        this.deltaX = BlockRayTrace.this.direction.getX() == 0 ? 0 : BlockRayTrace.this.stepX / BlockRayTrace.this.direction.getX();
        this.deltaY = BlockRayTrace.this.direction.getY() == 0 ? 0 : BlockRayTrace.this.stepY / BlockRayTrace.this.direction.getY();
        this.deltaZ = BlockRayTrace.this.direction.getZ() == 0 ? 0 : BlockRayTrace.this.stepZ / BlockRayTrace.this.direction.getZ();
    }

    public static BlockRayTrace of(Vector3f start, Vector3f direction, double distance) {
        return new BlockRayTrace(start, start.add(direction.mul(distance)), direction);
    }

    public static BlockRayTrace of(Vector3f start, Vector3f end) {
        return new BlockRayTrace(start, end, end.sub(start).normalize());
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

    public Vector3f getStart() {
        return start;
    }

    public Vector3f getEnd() {
        return end;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public double distance() {
        return this.start.distance(this.end);
    }

    @Nonnull
    @Override
    public Iterator<Vector3i> iterator() {
        return new BlockRayTraceIterator();
    }

    private class BlockRayTraceIterator implements Iterator<Vector3i> {
        private Vector3f currentBlock;

        private double maxX;
        private double maxY;
        private double maxZ;

        public BlockRayTraceIterator() {
            this.maxX = rayTraceDistanceToBoundary(BlockRayTrace.this.start.getX(), BlockRayTrace.this.direction.getX());
            this.maxY = rayTraceDistanceToBoundary(BlockRayTrace.this.start.getY(), BlockRayTrace.this.direction.getY());
            this.maxZ = rayTraceDistanceToBoundary(BlockRayTrace.this.start.getZ(), BlockRayTrace.this.direction.getZ());
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
        public Vector3i next() {
            if (currentBlock == null) {
                this.currentBlock = BlockRayTrace.this.start.floor();
                return currentBlock.toInt();
            }

            if (this.maxX < this.maxY && this.maxX < this.maxZ) {
                if (this.maxX > BlockRayTrace.this.distance()) {
                    return null;
                }
                this.currentBlock = this.currentBlock.add(BlockRayTrace.this.stepX, 0, 0);
                this.maxX += BlockRayTrace.this.deltaX;
            } else if (this.maxY < this.maxZ) {
                if (this.maxY > BlockRayTrace.this.distance()) {
                    return null;
                }
                this.currentBlock = this.currentBlock.add(0, BlockRayTrace.this.stepY, 0);
                this.maxY += BlockRayTrace.this.deltaY;
            } else {
                if (this.maxZ > BlockRayTrace.this.distance()) {
                    return null;
                }
                this.currentBlock = this.currentBlock.add(0, 0, BlockRayTrace.this.stepZ);
                this.maxZ += BlockRayTrace.this.deltaZ;
            }

            return currentBlock.toInt();
        }
    }
}

package cn.nukkit.math;

import cn.nukkit.Server;
import cn.nukkit.level.MovingObjectPosition;

/**
 * auth||: MagicDroidX
 * Nukkit Project
 */
public class AxisAlignedBB implements Cloneable {

    public double minX;
    public double minY;
    public double minZ;
    public double maxX;
    public double maxY;
    public double maxZ;

    public AxisAlignedBB(Vector3 pos1, Vector3 pos2) {
        this.minX = Math.min(pos1.x, pos2.x);
        this.minY = Math.min(pos1.y, pos2.y);
        this.minZ = Math.min(pos1.z, pos2.z);
        this.maxX = Math.max(pos1.x, pos2.x);
        this.maxY = Math.max(pos1.y, pos2.y);
        this.maxZ = Math.max(pos1.z, pos2.z);
    }

    public AxisAlignedBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public AxisAlignedBB setBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        return this;
    }

    public AxisAlignedBB addCoord(double x, double y, double z) {
        double minX = this.minX;
        double minY = this.minY;
        double minZ = this.minZ;
        double maxX = this.maxX;
        double maxY = this.maxY;
        double maxZ = this.maxZ;

        if (x < 0) minX += x;
        if (x > 0) maxX += x;

        if (y < 0) minY += y;
        if (y > 0) maxY += y;

        if (z < 0) minZ += z;
        if (z > 0) maxZ += z;

        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public AxisAlignedBB grow(double x, double y, double z) {
        return new AxisAlignedBB(this.minX - x, this.minY - y, this.minZ - z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    public AxisAlignedBB expand(double x, double y, double z)

    {
        this.minX -= x;
        this.minY -= y;
        this.minZ -= z;
        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;

        return this;
    }

    public AxisAlignedBB offset(double x, double y, double z) {
        this.minX += x;
        this.minY += y;
        this.minZ += z;
        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;

        return this;
    }

    public AxisAlignedBB shrink(double x, double y, double z) {
        return new AxisAlignedBB(this.minX + x, this.minY + y, this.minZ + z, this.maxX - x, this.maxY - y, this.maxZ - z);
    }

    public AxisAlignedBB contract(double x, double y, double z) {
        this.minX += x;
        this.minY += y;
        this.minZ += z;
        this.maxX -= x;
        this.maxY -= y;
        this.maxZ -= z;

        return this;
    }

    public AxisAlignedBB setBB(AxisAlignedBB bb) {
        this.minX = bb.minX;
        this.minY = bb.minY;
        this.minZ = bb.minZ;
        this.maxX = bb.maxX;
        this.maxY = bb.maxY;
        this.maxZ = bb.maxZ;
        return this;
    }

    public AxisAlignedBB getOffsetBoundingBox(double x, double y, double z) {
        return new AxisAlignedBB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    public double calculateXOffset(AxisAlignedBB bb, double x) {
        if (bb.maxY <= this.minY || bb.minY >= this.maxY) {
            return x;
        }
        if (bb.maxZ <= this.minZ || bb.minZ >= this.maxZ) {
            return x;
        }
        if (x > 0 && bb.maxX <= this.minX) {
            double x1 = this.minX - bb.maxX;
            if (x1 < x) {
                x = x1;
            }
        }
        if (x < 0 && bb.minX >= this.maxX) {
            double x2 = this.maxX - bb.minX;
            if (x2 > x) {
                x = x2;
            }
        }

        return x;
    }

    public double calculateYOffset(AxisAlignedBB bb, double y) {
        if (bb.maxX <= this.minX || bb.minX >= this.maxX) {
            return y;
        }
        if (bb.maxZ <= this.minZ || bb.minZ >= this.maxZ) {
            return y;
        }
        if (y > 0 && bb.maxY <= this.minY) {
            double y1 = this.minY - bb.maxY;
            if (y1 < y) {
                y = y1;
            }
        }
        if (y < 0 && bb.minY >= this.maxY) {
            double y2 = this.maxY - bb.minY;
            if (y2 > y) {
                y = y2;
            }
        }

        return y;
    }

    public double calculateZOffset(AxisAlignedBB bb, double z) {
        if (bb.maxX <= this.minX || bb.minX >= this.maxX) {
            return z;
        }
        if (bb.maxY <= this.minY || bb.minY >= this.maxY) {
            return z;
        }
        if (z > 0 && bb.maxZ <= this.minZ) {
            double z1 = this.minZ - bb.maxZ;
            if (z1 < z) {
                z = z1;
            }
        }
        if (z < 0 && bb.minZ >= this.maxZ) {
            double z2 = this.maxZ - bb.minZ;
            if (z2 > z) {
                z = z2;
            }
        }

        return z;
    }

    public boolean intersectsWith(AxisAlignedBB bb) {
        if (bb.maxX > this.minX && bb.minX < this.maxX) {
            if (bb.maxY > this.minY && bb.minY < this.maxY) {
                return bb.maxZ > this.minZ && bb.minZ < this.maxZ;
            }
        }

        return false;
    }

    public boolean isVectorInside(Vector3 vector) {
        return vector.x >= this.minX && vector.x <= this.maxX && vector.y >= this.minY && vector.y <= this.maxY && vector.z >= this.minZ && vector.z <= this.maxZ;

    }

    public double getAverageEdgeLength() {
        return (this.maxX - this.minX + this.maxY - this.minY + this.maxZ - this.minZ) / 3;
    }

    public boolean isVectorInYZ(Vector3 vector) {
        return vector.y >= this.minY && vector.y <= this.maxY && vector.z >= this.minZ && vector.z <= this.maxZ;
    }

    public boolean isVectorInXZ(Vector3 vector) {
        return vector.x >= this.minX && vector.x <= this.maxX && vector.z >= this.minZ && vector.z <= this.maxZ;
    }

    public boolean isVectorInXY(Vector3 vector) {
        return vector.x >= this.minX && vector.x <= this.maxX && vector.y >= this.minY && vector.y <= this.maxY;
    }

    public MovingObjectPosition calculateIntercept(Vector3 pos1, Vector3 pos2) {
        Vector3 v1 = pos1.getIntermediateWithXValue(pos2, this.minX);
        Vector3 v2 = pos1.getIntermediateWithXValue(pos2, this.maxX);
        Vector3 v3 = pos1.getIntermediateWithYValue(pos2, this.minY);
        Vector3 v4 = pos1.getIntermediateWithYValue(pos2, this.maxY);
        Vector3 v5 = pos1.getIntermediateWithZValue(pos2, this.minZ);
        Vector3 v6 = pos1.getIntermediateWithZValue(pos2, this.maxZ);

        if (v1 != null && !this.isVectorInYZ(v1)) {
            v1 = null;
        }

        if (v2 != null && !this.isVectorInYZ(v2)) {
            v2 = null;
        }

        if (v3 != null && !this.isVectorInXZ(v3)) {
            v3 = null;
        }

        if (v4 != null && !this.isVectorInXZ(v4)) {
            v4 = null;
        }

        if (v5 != null && !this.isVectorInXY(v5)) {
            v5 = null;
        }

        if (v6 != null && !this.isVectorInXY(v6)) {
            v6 = null;
        }

        Vector3 vector = null;

        //if (v1 != null && (vector == null || pos1.distanceSquared(v1) < pos1.distanceSquared(vector))) {
        if (v1 != null) {
            vector = v1;
        }

        if (v2 != null && (vector == null || pos1.distanceSquared(v2) < pos1.distanceSquared(vector))) {
            vector = v2;
        }

        if (v3 != null && (vector == null || pos1.distanceSquared(v3) < pos1.distanceSquared(vector))) {
            vector = v3;
        }

        if (v4 != null && (vector == null || pos1.distanceSquared(v4) < pos1.distanceSquared(vector))) {
            vector = v4;
        }

        if (v5 != null && (vector == null || pos1.distanceSquared(v5) < pos1.distanceSquared(vector))) {
            vector = v5;
        }

        if (v6 != null && (vector == null || pos1.distanceSquared(v6) < pos1.distanceSquared(vector))) {
            vector = v6;
        }

        if (vector == null) {
            return null;
        }

        int face = -1;

        if (vector == v1) {
            face = 4;
        } else if (vector == v2) {
            face = 5;
        } else if (vector == v3) {
            face = 0;
        } else if (vector == v4) {
            face = 1;
        } else if (vector == v5) {
            face = 2;
        } else if (vector == v6) {
            face = 3;
        }

        return MovingObjectPosition.fromBlock(0, 0, 0, face, vector);
    }

    @Override
    public String toString() {
        return "AxisAlignedBB(" + this.minX + ", " + this.minY + ", " + this.minZ + ", " + this.maxX + ", " + this.maxY + ", " + this.maxZ + ")";
    }

    @Override
    public AxisAlignedBB clone() {
        try {
            return (AxisAlignedBB) super.clone();
        } catch (CloneNotSupportedException e) {
            Server.getInstance().getLogger().logException(e);
        }
        return null;
    }
}

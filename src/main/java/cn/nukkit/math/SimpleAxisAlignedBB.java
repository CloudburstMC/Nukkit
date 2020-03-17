package cn.nukkit.math;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;

/**
 * auth||: MagicDroidX
 * Nukkit Project
 */
public class SimpleAxisAlignedBB implements AxisAlignedBB {

    private float minX;
    private float minY;
    private float minZ;
    private float maxX;
    private float maxY;
    private float maxZ;

    public SimpleAxisAlignedBB(Vector3i pos1, Vector3i pos2) {
        this.minX = Math.min(pos1.getX(), pos2.getX());
        this.minY = Math.min(pos1.getY(), pos2.getY());
        this.minZ = Math.min(pos1.getZ(), pos2.getZ());
        this.maxX = Math.max(pos1.getX(), pos2.getX());
        this.maxY = Math.max(pos1.getY(), pos2.getY());
        this.maxZ = Math.max(pos1.getZ(), pos2.getZ());
    }

    public SimpleAxisAlignedBB(Vector3f pos1, Vector3f pos2) {
        this.minX = Math.min(pos1.getX(), pos2.getX());
        this.minY = Math.min(pos1.getY(), pos2.getY());
        this.minZ = Math.min(pos1.getZ(), pos2.getZ());
        this.maxX = Math.max(pos1.getX(), pos2.getX());
        this.maxY = Math.max(pos1.getY(), pos2.getY());
        this.maxZ = Math.max(pos1.getZ(), pos2.getZ());
    }

    public SimpleAxisAlignedBB(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    @Override
    public String toString() {
        return "AxisAlignedBB(" + this.getMinX() + ", " + this.getMinY() + ", " + this.getMinZ() + ", " + this.getMaxX() + ", " + this.getMaxY() + ", " + this.getMaxZ() + ")";
    }

    @Override
    public float getMinX() {
        return minX;
    }

    @Override
    public void setMinX(float minX) {
        this.minX = minX;
    }

    @Override
    public float getMinY() {
        return minY;
    }

    @Override
    public void setMinY(float minY) {
        this.minY = minY;
    }

    @Override
    public float getMinZ() {
        return minZ;
    }

    @Override
    public void setMinZ(float minZ) {
        this.minZ = minZ;
    }

    @Override
    public float getMaxX() {
        return maxX;
    }

    @Override
    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    @Override
    public float getMaxY() {
        return maxY;
    }

    @Override
    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    @Override
    public float getMaxZ() {
        return maxZ;
    }

    @Override
    public void setMaxZ(float maxZ) {
        this.maxZ = maxZ;
    }

    @Override
    public AxisAlignedBB clone() {
        return new SimpleAxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}

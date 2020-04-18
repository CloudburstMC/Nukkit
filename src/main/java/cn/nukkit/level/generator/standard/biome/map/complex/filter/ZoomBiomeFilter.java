package cn.nukkit.level.generator.standard.biome.map.complex.filter;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.biome.map.complex.AbstractBiomeFilter;
import cn.nukkit.level.generator.standard.misc.IntArrayAllocator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Collection;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public class ZoomBiomeFilter extends AbstractBiomeFilter.Next {
    public static final Identifier ID = Identifier.fromString("nukkitx:zoom");

    @JsonProperty
    protected int times = 1;

    @JsonProperty
    protected Mode mode = Mode.DEFAULT;

    @Override
    public Collection<GenerationBiome> getAllBiomes() {
        return this.next.getAllBiomes();
    }

    @Override
    public int[] get(int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
        return this.times > 0
               ? this.getRecursive(x, z, sizeX, sizeZ, alloc, this.times)
               : this.next.get(x, z, sizeX, sizeZ, alloc);
    }

    protected int[] getRecursive(int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc, int depth) {
        int belowX = this.mode.belowCoord(x);
        int belowZ = this.mode.belowCoord(z);
        int belowSizeX = this.mode.belowSize(sizeX);
        int belowSizeZ = this.mode.belowSize(sizeZ);
        int[] below = depth == 1
                      ? this.next.get(belowX, belowZ, belowSizeX, belowSizeZ, alloc)
                      : this.getRecursive(belowX, belowZ, belowSizeX, belowSizeZ, alloc, depth - 1);

        return this.mode.doZoom(this, below, x, z, sizeX, sizeZ, alloc);
    }

    protected int selectNormal(int x, int z, int v0, int v1, int v2, int v3) {
        if (v1 == v2 && v2 == v3) {
            return v1;
        } else if (v0 == v1 && v0 == v2) {
            return v0;
        } else if (v0 == v1 && v0 == v3) {
            return v0;
        } else if (v0 == v2 && v0 == v3) {
            return v0;
        } else if (v0 == v1 && v2 != v3) {
            return v0;
        } else if (v0 == v2 && v1 != v3) {
            return v0;
        } else if (v0 == v3 && v1 != v2) {
            return v0;
        } else if (v1 == v2 && v0 != v3) {
            return v1;
        } else if (v1 == v3 && v0 != v2) {
            return v1;
        } else if (v2 == v3 && v0 != v1) {
            return v2;
        }

        return this.selectRandom(x, z, v0, v1, v2, v3);
    }

    protected int selectRandom(int x, int z, int v0, int v1, int v2, int v3) {
        switch (this.random(x, z, 0, 4)) {
            case 0:
                return v0;
            case 1:
                return v1;
            case 2:
                return v2;
            case 3:
                return v3;
            default:
                throw new IllegalStateException();
        }
    }

    @JsonSetter("mode")
    private void setMode(String mode) {
        this.mode = Mode.valueOf(mode.toUpperCase());
    }

    protected enum Mode {
        DEFAULT {
            @Override
            public int[] doZoom(ZoomBiomeFilter filter, int[] below, int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
                int belowX = this.belowCoord(x);
                int belowZ = this.belowCoord(z);
                int belowSizeX = this.belowSize(sizeX);
                int belowSizeZ = this.belowSize(sizeZ);

                int zoomSizeX = (belowSizeX - 1) << 1;
                int zoomSizeZ = (belowSizeZ - 1) << 1;
                int[] tmp = alloc.get(zoomSizeX * zoomSizeZ);
                for (int dx = 0; dx < belowSizeX - 1; dx++) {
                    for (int dz = 0; dz < belowSizeZ - 1; dz++) {
                        int i = (dx << 1) * zoomSizeZ + (dz << 1);

                        int rndX = (belowX + dx) << 1;
                        int rndZ = (belowZ + dz) << 1;

                        int v0 = below[dx * belowSizeZ + dz];
                        int v1 = below[dx * belowSizeZ + (dz + 1)];
                        int v2 = below[(dx + 1) * belowSizeZ + dz];
                        int v3 = below[(dx + 1) * belowSizeZ + (dz + 1)];

                        tmp[i] = v0;
                        tmp[i + 1] = filter.random(rndX, rndZ, 1, 2) == 0 ? v0 : v1;
                        tmp[i + zoomSizeZ] = filter.random(rndX, rndZ, 2, 2) == 0 ? v0 : v2;
                        tmp[i + zoomSizeZ + 1] = filter.selectNormal(rndX, rndZ, v0, v1, v2, v3);
                    }
                }
                alloc.release(below);

                int[] out = alloc.get(sizeX * sizeZ);
                for (int dx = 0; dx < sizeX; dx++) {
                    System.arraycopy(tmp, (dx + (x & 1)) * zoomSizeZ + (z & 1), out, dx * sizeZ, sizeZ);
                }
                alloc.release(tmp);

                return out;
            }
        },
        FUZZY {
            @Override
            public int[] doZoom(ZoomBiomeFilter filter, int[] below, int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
                int belowX = this.belowCoord(x);
                int belowZ = this.belowCoord(z);
                int belowSizeX = this.belowSize(sizeX);
                int belowSizeZ = this.belowSize(sizeZ);

                int zoomSizeX = (belowSizeX - 1) << 1;
                int zoomSizeZ = (belowSizeZ - 1) << 1;
                int[] tmp = alloc.get(zoomSizeX * zoomSizeZ);
                for (int dx = 0; dx < belowSizeX - 1; dx++) {
                    for (int dz = 0; dz < belowSizeZ - 1; dz++) {
                        int i = (dx << 1) * zoomSizeZ + (dz << 1);

                        int rndX = (belowX + dx) << 1;
                        int rndZ = (belowZ + dz) << 1;

                        int v0 = below[dx * belowSizeZ + dz];
                        int v1 = below[dx * belowSizeZ + (dz + 1)];
                        int v2 = below[(dx + 1) * belowSizeZ + dz];
                        int v3 = below[(dx + 1) * belowSizeZ + (dz + 1)];

                        tmp[i] = v0;
                        tmp[i + 1] = filter.random(rndX, rndZ, 1, 2) == 0 ? v0 : v1;
                        tmp[i + zoomSizeZ] = filter.random(rndX, rndZ, 2, 2) == 0 ? v0 : v2;
                        tmp[i + zoomSizeZ + 1] = filter.selectRandom(rndX, rndZ, v0, v1, v2, v3);
                    }
                }
                alloc.release(below);

                int[] out = alloc.get(sizeX * sizeZ);
                for (int dx = 0; dx < sizeX; dx++) {
                    System.arraycopy(tmp, (dx + (x & 1)) * zoomSizeZ + (z & 1), out, dx * sizeZ, sizeZ);
                }
                alloc.release(tmp);

                return out;
            }
        },
        VORONOI {
            @Override
            public int belowCoord(int parentCoord) {
                return parentCoord >> 2;
            }

            @Override
            public int belowSize(int parentSize) {
                return (parentSize >> 2) + 2;
            }

            @Override
            public int[] doZoom(ZoomBiomeFilter filter, int[] below, int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc) {
                int belowX = this.belowCoord(x);
                int belowZ = this.belowCoord(z);
                int belowSizeX = this.belowSize(sizeX);
                int belowSizeZ = this.belowSize(sizeZ);

                int zoomSizeX = (belowSizeX - 1) << 2;
                int zoomSizeZ = (belowSizeZ - 1) << 2;
                int[] tmp = alloc.get(zoomSizeX * zoomSizeZ);
                for (int dx = 0; dx < belowSizeX - 1; dx++) {
                    for (int dz = 0; dz < belowSizeZ - 1; dz++) {
                        int i = dx * belowSizeZ + dz;
                        int vxz = below[i];
                        int vxZ = below[i + 1];
                        int vXz = below[i + belowSizeZ];
                        int vXZ = below[i + belowSizeZ + 1];

                        int rndX = (belowX + dx) << 2;
                        int rndZ = (belowZ + dz) << 2;

                        double rxz0 = (filter.random(rndX, rndZ, 0, 1024) * 0.0009765625d - 0.5d) * 3.6d;
                        double rxz1 = (filter.random(rndX, rndZ, 1, 1024) * 0.0009765625d - 0.5d) * 3.6d;
                        double rXz0 = (filter.random(rndX + 4, rndZ, 0, 1024) * 0.0009765625d - 0.5d) * 3.6d + 4.0D;
                        double rXz1 = (filter.random(rndX + 4, rndZ, 1, 1024) * 0.0009765625d - 0.5d) * 3.6d;
                        double rxZ0 = (filter.random(rndX, rndZ + 4, 0, 1024) * 0.0009765625d - 0.5d) * 3.6d;
                        double rxZ1 = (filter.random(rndX, rndZ + 4, 1, 1024) * 0.0009765625d - 0.5d) * 3.6d + 4.0D;
                        double rXZ0 = (filter.random(rndX + 4, rndZ + 4, 0, 1024) * 0.0009765625d - 0.5d) * 3.6d + 4.0D;
                        double rXZ1 = (filter.random(rndX + 4, rndZ + 4, 1, 1024) * 0.0009765625d - 0.5d) * 3.6d + 4.0D;

                        for (int ddx = 0; ddx < 4; ddx++) {
                            i = ((dx << 2) + ddx) * zoomSizeZ + (dz << 2);
                            for (int ddz = 0; ddz < 4; ddz++, i++) {
                                double dxz = (ddz - rxz1) * (ddz - rxz1) + (ddx - rxz0) * (ddx - rxz0);
                                double dXz = (ddz - rXz1) * (ddz - rXz1) + (ddx - rXz0) * (ddx - rXz0);
                                double dxZ = (ddz - rxZ1) * (ddz - rxZ1) + (ddx - rxZ0) * (ddx - rxZ0);
                                double dXZ = (ddz - rXZ1) * (ddz - rXZ1) + (ddx - rXZ0) * (ddx - rXZ0);

                                if (dxz < dXz && dxz < dxZ && dxz < dXZ) {
                                    tmp[i] = vxz;
                                } else if (dXz < dxz && dXz < dxZ && dXz < dXZ) {
                                    tmp[i] = vXz;
                                } else if (dxZ < dxz && dxZ < dXz && dxZ < dXZ) {
                                    tmp[i] = vxZ;
                                } else {
                                    tmp[i] = vXZ;
                                }
                            }
                        }
                    }
                }
                alloc.release(below);

                int[] out = alloc.get(sizeX * sizeZ);
                for (int dx = 0; dx < sizeX; dx++) {
                    System.arraycopy(tmp, (dx + (x & 3)) * zoomSizeZ + (z & 3), out, dx * sizeZ, sizeZ);
                }
                alloc.release(tmp);

                return out;
            }
        };

        public int belowCoord(int parentCoord) {
            return parentCoord >> 1;
        }

        public int belowSize(int parentSize) {
            return (parentSize >> 1) + 2;
        }

        public abstract int[] doZoom(ZoomBiomeFilter filter, int[] below, int x, int z, int sizeX, int sizeZ, IntArrayAllocator alloc);
    }
}

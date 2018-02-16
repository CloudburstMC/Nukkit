package cn.nukkit.level.generator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.noise.Noise;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Generator {
    public static final int TYPE_OLD = 0;
    public static final int TYPE_INFINITE = 1;
    public static final int TYPE_FLAT = 2;
    public static final int TYPE_NETHER = 3;

    public abstract int getId();

    public int getDimension() {
        return Level.DIMENSION_OVERWORLD;
    }

    private static final Map<String, Class<? extends Generator>> nameList = new HashMap<>();

    private static final Map<Integer, Class<? extends Generator>> typeList = new HashMap<>();

    public static boolean addGenerator(Class<? extends Generator> clazz, String name, int type) {
        name = name.toLowerCase();
        if (clazz != null && !Generator.nameList.containsKey(name)) {
            Generator.nameList.put(name, clazz);
            if (!Generator.typeList.containsKey(type)) {
                Generator.typeList.put(type, clazz);
            }
            return true;
        }
        return false;
    }

    public static String[] getGeneratorList() {
        String[] keys = new String[Generator.nameList.size()];
        return Generator.nameList.keySet().toArray(keys);
    }

    public static Class<? extends Generator> getGenerator(String name) {
        name = name.toLowerCase();
        if (Generator.nameList.containsKey(name)) {
            return Generator.nameList.get(name);
        }
        return Normal.class;
    }

    public static Class<? extends Generator> getGenerator(int type) {
        if (Generator.typeList.containsKey(type)) {
            return Generator.typeList.get(type);
        }
        return Normal.class;
    }

    public static String getGeneratorName(Class<? extends Generator> c) {
        for (String key : Generator.nameList.keySet()) {
            if (Generator.nameList.get(key).equals(c)) {
                return key;
            }
        }
        return "unknown";
    }

    public static int getGeneratorType(Class<? extends Generator> c) {
        for (int key : Generator.typeList.keySet()) {
            if (Generator.typeList.get(key).equals(c)) {
                return key;
            }
        }
        return Generator.TYPE_INFINITE;
    }

    @Deprecated
    public static double[] getFastNoise1D(Noise noise, int xSize, int samplingRate, int x, int y, int z) {
        if (samplingRate == 0) {
            throw new IllegalArgumentException("samplingRate cannot be 0");
        }
        if (xSize % samplingRate != 0) {
            throw new IllegalArgumentException("xSize % samplingRate must return 0");
        }
        double[] noiseArray = new double[xSize + 1];

        for (int xx = 0; xx <= xSize; xx += samplingRate) {
            noiseArray[xx] = noise.noise3D(xx + x, y, z);
        }

        for (int xx = 0; xx < xSize; ++xx) {
            if (xx % samplingRate != 0) {
                int nx = xx / samplingRate * samplingRate;
                noiseArray[nx] = Noise.linearLerp(xx, nx, nx + samplingRate, noiseArray[nx], noiseArray[nx + samplingRate]);
            }
        }

        return noiseArray;
    }

    public static void getFastNoise1D(Noise noise, double[] noiseArray, int xSize, int samplingRate, int x, int y, int z) {
        if (samplingRate == 0) {
            throw new IllegalArgumentException("samplingRate cannot be 0");
        }
        if (xSize % samplingRate != 0) {
            throw new IllegalArgumentException("xSize % samplingRate must return 0");
        }
        if (noiseArray.length != xSize) {
            throw new IllegalArgumentException("Length of noiseArray must be the same as xSize");
        }

        for (int xx = 0; xx <= xSize; xx += samplingRate) {
            noiseArray[xx] = noise.noise3D(xx + x, y, z);
        }

        for (int xx = 0; xx < xSize; ++xx) {
            if (xx % samplingRate != 0) {
                int nx = xx / samplingRate * samplingRate;
                noiseArray[nx] = Noise.linearLerp(xx, nx, nx + samplingRate, noiseArray[nx], noiseArray[nx + samplingRate]);
            }
        }
    }

    @Deprecated
    public static double[][] getFastNoise2D(Noise noise, int xSize, int zSize, int samplingRate, int x, int y, int z, int xZoom, int zZoom) {
        if (samplingRate == 0) {
            throw new IllegalArgumentException("samplingRate cannot be 0");
        }
        if (xSize % samplingRate != 0) {
            throw new IllegalArgumentException("xSize % samplingRate must return 0");
        }
        if (zSize % samplingRate != 0) {
            throw new IllegalArgumentException("zSize % samplingRate must return 0");
        }

        double[][] noiseArray = new double[xSize + 1][zSize + 1];

        for (int xx = 0; xx <= xSize; xx += samplingRate) {
            noiseArray[xx] = new double[zSize + 1];
            for (int zz = 0; zz <= zSize; zz += samplingRate) {
                noiseArray[xx][zz] = noise.noise3D((x + xx) >> xZoom, y, (z + zz) >> zZoom);
            }
        }

        for (int xx = 0; xx < xSize; ++xx) {
            if (xx % samplingRate != 0) {
                noiseArray[xx] = new double[zSize + 1];
            }

            for (int zz = 0; zz < zSize; ++zz) {
                if (xx % samplingRate != 0 || zz % samplingRate != 0) {
                    int nx = xx / samplingRate * samplingRate;
                    int nz = zz / samplingRate * samplingRate;
                    noiseArray[xx][zz] = Noise.bilinearLerp(
                            xx, zz, noiseArray[nx][nz], noiseArray[nx][nz + samplingRate],
                            noiseArray[nx + samplingRate][nz], noiseArray[nx + samplingRate][nz + samplingRate],
                            nx, nx + samplingRate, nz, nz + samplingRate
                    );
                }
            }
        }
        return noiseArray;
    }

    public static void getFastNoise2D(Noise noise, double[][] noiseArray, int xSize, int zSize, int samplingRate, int x, int y, int z, int xZoom, int zZoom) {
        if (samplingRate == 0) {
            throw new IllegalArgumentException("samplingRate cannot be 0");
        }
        if (xSize % samplingRate != 0) {
            throw new IllegalArgumentException("xSize % samplingRate must return 0");
        }
        if (zSize % samplingRate != 0) {
            throw new IllegalArgumentException("zSize % samplingRate must return 0");
        }
        if (noiseArray.length != xSize) {
            throw new IllegalArgumentException("Length of noiseArray must be the same as xSize");
        }

        for (int xx = 0; xx <= xSize; xx += samplingRate) {
            for (int zz = 0; zz <= zSize; zz += samplingRate) {
                noiseArray[xx][zz] = noise.noise3D((x + xx) >> xZoom, y, (z + zz) >> zZoom);
            }
        }

        for (int xx = 0; xx < xSize; ++xx) {
            for (int zz = 0; zz < zSize; ++zz) {
                if (xx % samplingRate != 0 || zz % samplingRate != 0) {
                    int nx = xx / samplingRate * samplingRate;
                    int nz = zz / samplingRate * samplingRate;
                    noiseArray[xx][zz] = Noise.bilinearLerp(
                            xx, zz, noiseArray[nx][nz], noiseArray[nx][nz + samplingRate],
                            noiseArray[nx + samplingRate][nz], noiseArray[nx + samplingRate][nz + samplingRate],
                            nx, nx + samplingRate, nz, nz + samplingRate
                    );
                }
            }
        }
    }

    @Deprecated
    public static double[][] getFastNoise2D(Noise noise, int xSize, int zSize, int samplingRate, int x, int y, int z) {
        return Generator.getFastNoise2D(noise, xSize, zSize, samplingRate, x, y, z, 0, 0);
    }

    public static void getFastNoise2D(Noise noise, double[][] noiseArray, int xSize, int zSize, int samplingRate, int x, int y, int z) {
        Generator.getFastNoise2D(noise, noiseArray, xSize, zSize, samplingRate, x, y, z, 0, 0);
    }

    @Deprecated
    public static double[][][] getFastNoise3D(Noise noise, int xSize, int ySize, int zSize, int xSamplingRate, int ySamplingRate, int zSamplingRate, int x, int y, int z) {
        if (xSamplingRate == 0) {
            throw new IllegalArgumentException("xSamplingRate cannot be 0");
        }
        if (zSamplingRate == 0) {
            throw new IllegalArgumentException("zSamplingRate cannot be 0");
        }
        if (ySamplingRate == 0) {
            throw new IllegalArgumentException("ySamplingRate cannot be 0");
        }
        if (xSize % xSamplingRate != 0) {
            throw new IllegalArgumentException("xSize % xSamplingRate must return 0");
        }
        if (zSize % zSamplingRate != 0) {
            throw new IllegalArgumentException("zSize % zSamplingRate must return 0");
        }
        if (ySize % ySamplingRate != 0) {
            throw new IllegalArgumentException("ySize % ySamplingRate must return 0");
        }

        double[][][] noiseArray = new double[xSize + 1][zSize + 1][ySize + 1];
        for (int xx = 0; xx <= xSize; xx += xSamplingRate) {
            for (int zz = 0; zz <= zSize; zz += zSamplingRate) {
                for (int yy = 0; yy <= ySize; yy += ySamplingRate) {
                    noiseArray[xx][zz][yy] = noise.noise3D(x + xx, y + yy, z + zz, true);
                }
            }
        }

        for (int xx = 0; xx < xSize; ++xx) {
            for (int zz = 0; zz < zSize; ++zz) {
                for (int yy = 0; yy < ySize; ++yy) {
                    if (xx % xSamplingRate != 0 || zz % zSamplingRate != 0 || yy % ySamplingRate != 0) {
                        int nx = xx / xSamplingRate * xSamplingRate;
                        int ny = yy / ySamplingRate * ySamplingRate;
                        int nz = zz / zSamplingRate * zSamplingRate;

                        int nnx = nx + xSamplingRate;
                        int nny = ny + ySamplingRate;
                        int nnz = nz + zSamplingRate;

                        double dx1 = ((double) (nnx - xx) / (double) (nnx - nx));
                        double dx2 = ((double) (xx - nx) / (double) (nnx - nx));
                        double dy1 = ((double) (nny - yy) / (double) (nny - ny));
                        double dy2 = ((double) (yy - ny) / (double) (nny - ny));

                        noiseArray[xx][zz][yy] = ((double) (nnz - zz) / (double) (nnz - nz)) * (
                                dy1 * (
                                        dx1 * noiseArray[nx][nz][ny] + dx2 * noiseArray[nnx][nz][ny]
                                ) + dy2 * (
                                        dx1 * noiseArray[nx][nz][nny] + dx2 * noiseArray[nnx][nz][nny]
                                )
                        ) + ((double) (zz - nz) / (double) (nnz - nz)) * (
                                dy1 * (
                                        dx1 * noiseArray[nx][nnz][ny] + dx2 * noiseArray[nnx][nnz][ny]
                                ) + dy2 * (
                                        dx1 * noiseArray[nx][nnz][nny] + dx2 * noiseArray[nnx][nnz][nny]
                                )
                        );
                    }
                }
            }
        }

        return noiseArray;
    }

    public static void getFastNoise3D(Noise noise, double[][][] noiseArray, int xSize, int ySize, int zSize, int xSamplingRate, int ySamplingRate, int zSamplingRate, int x, int y, int z) {
        if (xSamplingRate == 0) {
            throw new IllegalArgumentException("xSamplingRate cannot be 0");
        }
        if (zSamplingRate == 0) {
            throw new IllegalArgumentException("zSamplingRate cannot be 0");
        }
        if (ySamplingRate == 0) {
            throw new IllegalArgumentException("ySamplingRate cannot be 0");
        }
        if (xSize % xSamplingRate != 0) {
            throw new IllegalArgumentException("xSize % xSamplingRate must return 0");
        }
        if (zSize % zSamplingRate != 0) {
            throw new IllegalArgumentException("zSize % zSamplingRate must return 0");
        }
        if (ySize % ySamplingRate != 0) {
            throw new IllegalArgumentException("ySize % ySamplingRate must return 0");
        }
        if (noiseArray.length != xSize) {
            throw new IllegalArgumentException("Length of noiseArray must be the same as xSize");
        }

        for (int xx = 0; xx <= xSize; xx += xSamplingRate) {
            for (int zz = 0; zz <= zSize; zz += zSamplingRate) {
                for (int yy = 0; yy <= ySize; yy += ySamplingRate) {
                    noiseArray[xx][zz][yy] = noise.noise3D(x + xx, y + yy, z + zz, true);
                }
            }
        }

        for (int xx = 0; xx < xSize; ++xx) {
            for (int zz = 0; zz < zSize; ++zz) {
                for (int yy = 0; yy < ySize; ++yy) {
                    if (xx % xSamplingRate != 0 || zz % zSamplingRate != 0 || yy % ySamplingRate != 0) {
                        int nx = xx / xSamplingRate * xSamplingRate;
                        int ny = yy / ySamplingRate * ySamplingRate;
                        int nz = zz / zSamplingRate * zSamplingRate;

                        int nnx = nx + xSamplingRate;
                        int nny = ny + ySamplingRate;
                        int nnz = nz + zSamplingRate;

                        double dx1 = ((double) (nnx - xx) / (double) (nnx - nx));
                        double dx2 = ((double) (xx - nx) / (double) (nnx - nx));
                        double dy1 = ((double) (nny - yy) / (double) (nny - ny));
                        double dy2 = ((double) (yy - ny) / (double) (nny - ny));

                        noiseArray[xx][zz][yy] = ((double) (nnz - zz) / (double) (nnz - nz)) * (
                                dy1 * (
                                        dx1 * noiseArray[nx][nz][ny] + dx2 * noiseArray[nnx][nz][ny]
                                ) + dy2 * (
                                        dx1 * noiseArray[nx][nz][nny] + dx2 * noiseArray[nnx][nz][nny]
                                )
                        ) + ((double) (zz - nz) / (double) (nnz - nz)) * (
                                dy1 * (
                                        dx1 * noiseArray[nx][nnz][ny] + dx2 * noiseArray[nnx][nnz][ny]
                                ) + dy2 * (
                                        dx1 * noiseArray[nx][nnz][nny] + dx2 * noiseArray[nnx][nnz][nny]
                                )
                        );
                    }
                }
            }
        }
    }

    public abstract void init(ChunkManager level, NukkitRandom random);

    public abstract void generateChunk(int chunkX, int chunkZ);

    public abstract void populateChunk(int chunkX, int chunkZ);

    public abstract Map<String, Object> getSettings();

    public abstract String getName();

    public abstract Vector3 getSpawn();

    public abstract ChunkManager getChunkManager();
}

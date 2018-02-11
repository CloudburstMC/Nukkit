    package cn.nukkit.level.generator.task;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.SimpleChunkManager;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.scheduler.AsyncTask;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PopulationTask extends AsyncTask {
    private final long seed;
    private final Level level;
    private boolean state;
    private BaseFullChunk centerChunk;

    public final BaseFullChunk[] chunks = new BaseFullChunk[9];

    public PopulationTask(Level level, BaseFullChunk chunk) {
        this.state = true;
        this.level = level;
        this.centerChunk = chunk;
        this.seed = level.getSeed();

        int i = 0;
        for (int z = -1; z <= 1; z++) {
            for (int x = -1; x <= 1; x++, i++) {
                if (i == 4) continue;
                BaseFullChunk ck = level.getChunk(chunk.getX() + x, chunk.getZ() + z, false);
                this.chunks[i] = ck;
            }
        }
    }

    @Override
    public void onRun() {
        this.state = false;
        Generator generator = level.getGenerator();
        if (generator == null) {
            return;
        }

        SimpleChunkManager manager = (SimpleChunkManager) generator.getChunkManager();

        if (manager == null) {
            this.state = false;
            return;
        }

        synchronized (manager) {
            try {
                manager.cleanChunks(this.seed);
                BaseFullChunk centerChunk = this.centerChunk.clone();

                if (centerChunk == null) {
                    return;
                }

                this.chunks[4] = centerChunk;

                int index = 0;
                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++, index++) {
                        BaseFullChunk ck = this.chunks[index];
                        if (ck == centerChunk) continue;
                        if (ck == null) {
                            try {
                                this.chunks[index] = (BaseFullChunk) centerChunk.getClass().getMethod("getEmptyChunk", int.class, int.class).invoke(null, centerChunk.getX() + x, centerChunk.getZ() + z);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            this.chunks[index] = ck.clone();
                        }

                    }
                }

                for (BaseFullChunk chunk : this.chunks) {
                    manager.setChunk(chunk.getX(), chunk.getZ(), chunk);
                    if (!chunk.isGenerated()) {
                        generator.generateChunk(chunk.getX(), chunk.getZ());
                        BaseFullChunk newChunk = manager.getChunk(chunk.getX(), chunk.getZ());
                        newChunk.setGenerated();
                        if (newChunk != chunk) manager.setChunk(chunk.getX(), chunk.getZ(), newChunk);
                    }
                }

                generator.populateChunk(centerChunk.getX(), centerChunk.getZ());

                centerChunk = manager.getChunk(centerChunk.getX(), centerChunk.getZ());
                centerChunk.recalculateHeightMap();
                centerChunk.populateSkyLight();
                centerChunk.setLightPopulated();
                centerChunk.setPopulated();
                this.centerChunk = centerChunk;

                manager.setChunk(centerChunk.getX(), centerChunk.getZ());

                index = 0;
                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++, index++) {
                        chunks[index] = null;
                        BaseFullChunk newChunk = manager.getChunk(centerChunk.getX() + x, centerChunk.getZ() + z);
                        if (newChunk != null) {
                            if (newChunk.hasChanged()) {
                                chunks[index] = newChunk;
                            }
                        }

                    }
                }
                this.state = true;
            } finally {
                manager.cleanChunks(this.seed);
            }
        }
    }

    @Override
    public void onCompletion(Server server) {
        if (level != null) {
            if (!this.state) {
                return;
            }

            BaseFullChunk centerChunk = this.centerChunk;

            if (centerChunk == null) {
                return;
            }

            for (BaseFullChunk chunk : this.chunks) {
                if (chunk != null) {
                    level.generateChunkCallback(chunk.getX(), chunk.getZ(), chunk);
                }
            }

            level.generateChunkCallback(centerChunk.getX(), centerChunk.getZ(), centerChunk);
        }
    }
}

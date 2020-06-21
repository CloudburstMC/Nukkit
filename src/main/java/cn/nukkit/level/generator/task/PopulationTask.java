package cn.nukkit.level.generator.task;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.SimpleChunkManager;
import cn.nukkit.scheduler.AsyncTask;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PopulationTask extends AsyncTask {

    public final BaseFullChunk[] chunks = new BaseFullChunk[9];

    private final long seed;

    private final Level level;

    private boolean state;

    private BaseFullChunk centerChunk;

    private boolean isPopulated;

    public PopulationTask(final Level level, final BaseFullChunk chunk) {
        this.state = true;
        this.level = level;
        this.centerChunk = chunk;
        this.seed = level.getSeed();

        this.chunks[4] = chunk;

        int i = 0;
        for (int z = -1; z <= 1; z++) {
            for (int x = -1; x <= 1; x++, i++) {
                if (i == 4) {
                    continue;
                }
                final BaseFullChunk ck = level.getChunk(chunk.getX() + x, chunk.getZ() + z, true);
                this.chunks[i] = ck;
            }
        }
    }

    @Override
    public void onRun() {
        this.syncGen(0);
    }

    @Override
    public void onCompletion(final Server server) {
        if (this.level != null) {
            if (!this.state) {
                return;
            }

            final BaseFullChunk centerChunk = this.centerChunk;

            if (centerChunk == null) {
                return;
            }

            for (final BaseFullChunk chunk : this.chunks) {
                if (chunk != null) {
                    this.level.generateChunkCallback(chunk.getX(), chunk.getZ(), chunk);
                }
            }

            this.level.generateChunkCallback(centerChunk.getX(), centerChunk.getZ(), centerChunk, this.isPopulated);
        }
    }

    private void syncGen(final int i) {
        if (i == this.chunks.length) {
            this.generationTask();
        } else {
            final BaseFullChunk chunk = this.chunks[i];
            if (chunk != null) {
                synchronized (chunk) {
                    this.syncGen(i + 1);
                }
            }
        }
    }

    private void generationTask() {
        this.state = false;
        final Generator generator = this.level.getGenerator();
        if (generator == null) {
            return;
        }

        final SimpleChunkManager manager = (SimpleChunkManager) generator.getChunkManager();

        if (manager == null) {
            this.state = false;
            return;
        }

        synchronized (manager) {
            try {
                manager.cleanChunks(this.seed);
                BaseFullChunk centerChunk = this.centerChunk;

                if (centerChunk == null) {
                    return;
                }

                int index = 0;
                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++, index++) {
                        final BaseFullChunk ck = this.chunks[index];
                        if (ck == centerChunk) {
                            continue;
                        }
                        if (ck == null) {
                            try {
                                this.chunks[index] = (BaseFullChunk) centerChunk.getClass().getMethod("getEmptyChunk", int.class, int.class).invoke(null, centerChunk.getX() + x, centerChunk.getZ() + z);
                            } catch (final Exception e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            this.chunks[index] = ck;
                        }

                    }
                }

                for (final BaseFullChunk chunk : this.chunks) {
                    manager.setChunk(chunk.getX(), chunk.getZ(), chunk);
                    if (!chunk.isGenerated()) {
                        generator.generateChunk(chunk.getX(), chunk.getZ());
                        final BaseFullChunk newChunk = manager.getChunk(chunk.getX(), chunk.getZ());
                        newChunk.setGenerated();
                        if (newChunk != chunk) {
                            manager.setChunk(chunk.getX(), chunk.getZ(), newChunk);
                        }
                    }
                }

                this.isPopulated = centerChunk.isPopulated();
                if (!this.isPopulated) {
                    generator.populateChunk(centerChunk.getX(), centerChunk.getZ());
                    centerChunk = manager.getChunk(centerChunk.getX(), centerChunk.getZ());
                    centerChunk.setPopulated();
                    centerChunk.recalculateHeightMap();
                    centerChunk.populateSkyLight();
                    centerChunk.setLightPopulated();
                    this.centerChunk = centerChunk;
                }

                manager.setChunk(centerChunk.getX(), centerChunk.getZ());

                index = 0;
                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++, index++) {
                        this.chunks[index] = null;
                        final BaseFullChunk newChunk = manager.getChunk(centerChunk.getX() + x, centerChunk.getZ() + z);
                        if (newChunk != null) {
                            if (newChunk.hasChanged()) {
                                this.chunks[index] = newChunk;
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

}

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
public class GenerationTask extends AsyncTask {
    public boolean state;
    public final int levelId;
    public BaseFullChunk chunk;

    public GenerationTask(Level level, BaseFullChunk chunk) {
        this.state = true;
        this.levelId = level.getId();
        this.chunk = chunk;
    }

    @Override
    public void onRun() {
        if (generator == null) {
            this.state = false;
            return;
        }

        SimpleChunkManager manager = (SimpleChunkManager) generator.getChunkManager();

        if (manager == null) {
            this.state = false;
            return;
        }

        synchronized (manager) {
            BaseFullChunk chunk = this.chunk.clone();

            if (chunk == null) {
                return;
            }

            manager.setChunk(chunk.getX(), chunk.getZ(), chunk);

            generator.generateChunk(chunk.getX(), chunk.getZ());

            chunk = manager.getChunk(chunk.getX(), chunk.getZ());
            chunk.setGenerated();
            this.chunk = chunk.clone();

            manager.setChunk(chunk.getX(), chunk.getZ(), null);
        }

    }

    @Override
    public void onCompletion(Server server) {
        Level level = server.getLevel(this.levelId);
        if (level != null) {
            if (!this.state) {
                level.registerGenerator();
                return;
            }

            BaseFullChunk chunk = this.chunk.clone();

            if (chunk == null) {
                return;
            }

            level.generateChunkCallback(chunk.getX(), chunk.getZ(), chunk);
        }
    }
}

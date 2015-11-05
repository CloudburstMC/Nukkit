package cn.nukkit.level.generator;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.scheduler.AsyncTask;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LightPopulationTask extends AsyncTask {

    public int levelId;
    public FullChunk chunk;

    public LightPopulationTask(Level level, FullChunk chunk) {
        this.levelId = level.getId();
        this.chunk = chunk;
        /*this.chunk = chunk.toFastBinary();
        this.chunkClass = chunk.getClass();*/
    }

    @Override
    public void onRun() {
        FullChunk chunk = ((BaseFullChunk) this.chunk).clone();
        if (chunk == null) {
            return;
        }

        chunk.recalculateHeightMap();
        chunk.populateSkyLight();
        chunk.setLightPopulated();

        this.chunk = chunk;
    }

    @Override
    public void onCompletion(Server server) {
        Level level = server.getLevel(this.levelId);

        FullChunk chunk = ((BaseFullChunk) this.chunk).clone();
        if (level != null) {
            if (chunk == null) {
                return;
            }

            level.generateChunkCallback(chunk.getX(), chunk.getZ(), chunk);
        }
    }
}

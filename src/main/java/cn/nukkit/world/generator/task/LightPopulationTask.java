package cn.nukkit.world.generator.task;

import cn.nukkit.Server;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.world.World;
import cn.nukkit.world.format.generic.BaseFullChunk;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LightPopulationTask extends AsyncTask {

    public final int levelId;
    public BaseFullChunk chunk;

    public LightPopulationTask(World level, BaseFullChunk chunk) {
        this.levelId = level.getId();
        this.chunk = chunk;
    }

    @Override
    public void onRun() {
        BaseFullChunk chunk = this.chunk.clone();
        if (chunk == null) {
            return;
        }

        chunk.recalculateHeightMap();
        chunk.populateSkyLight();
        chunk.setLightPopulated();

        this.chunk = chunk.clone();
    }

    @Override
    public void onCompletion(Server server) {
        World level = server.getWorld(this.levelId);

        BaseFullChunk chunk = this.chunk.clone();
        if (level != null) {
            if (chunk == null) {
                return;
            }

            level.generateChunkCallback(chunk.getX(), chunk.getZ(), chunk);
        }
    }
}

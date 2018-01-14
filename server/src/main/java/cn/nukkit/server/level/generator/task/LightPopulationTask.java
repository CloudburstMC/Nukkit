package cn.nukkit.server.level.generator.task;

import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.level.format.generic.BaseFullChunk;
import cn.nukkit.server.scheduler.AsyncTask;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LightPopulationTask extends AsyncTask {

    public final int levelId;
    public BaseFullChunk chunk;

    public LightPopulationTask(NukkitLevel level, BaseFullChunk chunk) {
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
    public void onCompletion(NukkitServer server) {
        NukkitLevel level = server.getLevel(this.levelId);

        BaseFullChunk chunk = this.chunk.clone();
        if (level != null) {
            if (chunk == null) {
                return;
            }

            level.generateChunkCallback(chunk.getX(), chunk.getZ(), chunk);
        }
    }
}

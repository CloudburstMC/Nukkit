package cn.nukkit.level.generator.task;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.Task;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LightPopulationTask extends Task {

    public final int levelId;
    public BaseFullChunk chunk;

    public LightPopulationTask(Level level, BaseFullChunk chunk) {
        this.levelId = level.getId();
        this.chunk = chunk;
    }

    @Override
    public void onRun(int i) {
        try {
            BaseFullChunk chunk = this.chunk.clone();
            if (chunk == null) {
                return;
            }

            chunk.recalculateHeightMap();
            chunk.populateSkyLight();
            chunk.setLightPopulated();

            this.chunk = chunk.clone();
        } finally {
            Level level = Server.getInstance().getLevel(this.levelId);

            BaseFullChunk chunk = this.chunk.clone();
            if (level != null) {
                if (chunk == null) {
                    // ignore
                } else {
                    level.generateChunkCallback(chunk.getX(), chunk.getZ(), chunk);
                }
            }
        }
    }
}

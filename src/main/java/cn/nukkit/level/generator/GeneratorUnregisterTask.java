package cn.nukkit.level.generator;

import cn.nukkit.level.ChunkManagerPool;
import cn.nukkit.level.Level;
import cn.nukkit.scheduler.AsyncTask;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GeneratorUnregisterTask extends AsyncTask {

    public int levelId;

    public GeneratorUnregisterTask(Level level) {
        this.levelId = level.getId();
    }

    @Override
    public void onRun() {
        ChunkManagerPool.remove(levelId);
        GeneratorPool.remove(levelId);
    }
}

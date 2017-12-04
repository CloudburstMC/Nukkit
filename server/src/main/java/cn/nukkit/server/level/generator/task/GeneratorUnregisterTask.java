package cn.nukkit.server.level.generator.task;

import cn.nukkit.server.level.Level;
import cn.nukkit.server.scheduler.AsyncTask;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GeneratorUnregisterTask extends AsyncTask {

    public final int levelId;

    public GeneratorUnregisterTask(Level level) {
        this.levelId = level.getId();
    }

    @Override
    public void onRun() {
        GeneratorPool.remove(levelId);
    }
}

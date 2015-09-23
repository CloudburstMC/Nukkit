package cn.nukkit.level.generator;

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
        this.saveToThreadStore("generation.level" + this.levelId + ".manager", null);
        this.saveToThreadStore("generation.level" + this.levelId + ".generator", null);
    }
}

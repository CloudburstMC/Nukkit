package cn.nukkit.level.generator;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.scheduler.AsyncTask;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LightPopulationTask extends AsyncTask {

    public int levelId;
    public byte[] chunk;
    public Class<? extends FullChunk> chunkClass;

    public LightPopulationTask(Level level, FullChunk chunk) {
        this.levelId = level.getId();
        this.chunk = chunk.toFastBinary();
        this.chunkClass = chunk.getClass();
    }

    @Override
    public void onRun() {
        //todo
    }
}

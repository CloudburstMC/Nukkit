package cn.nukkit.level.generator;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.scheduler.AsyncTask;

public interface GeneratorTaskFactory {

    AsyncTask populateChunkTask(BaseFullChunk chunk, Level level);
    AsyncTask generateChunkTask(BaseFullChunk chunk, Level level);
}

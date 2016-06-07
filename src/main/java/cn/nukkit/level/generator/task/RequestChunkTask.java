package cn.nukkit.level.generator.task;

import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.scheduler.AsyncTask;

public abstract class RequestChunkTask extends AsyncTask {
	public abstract BaseFullChunk getChunk();
}

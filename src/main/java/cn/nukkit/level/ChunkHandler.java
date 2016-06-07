package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.level.format.generic.BaseFullChunk;

public abstract class ChunkHandler {
	
	public abstract void onRun(BaseFullChunk chunk, Server server);
}

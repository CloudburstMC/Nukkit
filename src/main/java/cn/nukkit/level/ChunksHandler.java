package cn.nukkit.level;

import java.util.ArrayList;

import cn.nukkit.Server;
import cn.nukkit.level.format.generic.BaseFullChunk;

public abstract class ChunksHandler {
	public abstract void onRun(ArrayList<BaseFullChunk> chunks, Server server);
}

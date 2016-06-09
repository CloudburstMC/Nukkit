package cn.nukkit.level;

import java.util.LinkedHashMap;

import cn.nukkit.level.format.generic.BaseRegionLoader;

public class RegionPool {
	public static LinkedHashMap<String, BaseRegionLoader> map = new LinkedHashMap<>();

	public static BaseRegionLoader getRegion(int levelId, int regionX, int regionZ) {
		String index = levelId + ":" + regionX + ":" + regionZ;
		return map.containsKey(index) ? map.get(index) : null;
	}

	public static void setRegion(int levelId, int regionX, int regionZ, BaseRegionLoader loader) {
		map.put(levelId + ":" + regionX + ":" + regionZ, loader);
	}
}
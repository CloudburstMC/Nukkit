package cn.nukkit.level;

import cn.nukkit.level.provider.LevelProvider;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class LevelConverter {
    private final LevelProvider oldChunkProvider;
    private final LevelProvider newChunkProvider;

    public LevelConverter(LevelProvider oldChunkProvider, LevelProvider newChunkProvider) {
        this.oldChunkProvider = oldChunkProvider;
        this.newChunkProvider = newChunkProvider;
    }

    public void perform() {
        this.oldChunkProvider.forEachChunk((chunk, throwable) -> {
            if (throwable != null) {
                log.error("Unable to convert chunk", throwable);
                return;
            }

            if (chunk != null) {
                log.debug("Saving chunk ({},{})", chunk.getX(), chunk.getZ());
                this.newChunkProvider.saveChunk(chunk);
            }
        });
    }
}

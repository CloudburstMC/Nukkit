package cn.nukkit.level.provider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Executor;

public interface LevelProviderFactory {

    /**
     * Creates new provider
     *
     * @param levelId    level ID
     * @param levelsPath path of the levels directory (NOT THE LEVEL DIRECTORY ITSELF)
     * @param executor   executor to run tasks async
     * @return chunk provider
     * @throws IOException error created provider
     */
    LevelProvider create(String levelId, Path levelsPath, Executor executor) throws IOException;

    /**
     * Checks if level provider is compatible with directory given
     *
     * @param levelsPath path of the levels directory (NOT THE LEVEL DIRECTORY ITSELF)
     * @param levelId    level ID
     * @return true if level is compatible.
     */
    boolean isCompatible(String levelId, Path levelsPath);
}

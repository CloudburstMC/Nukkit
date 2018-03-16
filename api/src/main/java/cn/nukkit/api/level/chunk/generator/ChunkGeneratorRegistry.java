package cn.nukkit.api.level.chunk.generator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public interface ChunkGeneratorRegistry {

    Optional<ChunkGenerator> getChunkGenerator(String name);

    ChunkGenerator ensureAndGetChunkGenerator(String name);

    void register(String name, ChunkGeneratorFactory factory);

    void deregister(String name);
}

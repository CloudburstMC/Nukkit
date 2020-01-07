package cn.nukkit.level.provider.leveldb;

import cn.nukkit.level.provider.LevelProvider;
import cn.nukkit.level.provider.LevelProviderFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Executor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LevelDBProviderFactory implements LevelProviderFactory {

    public static final LevelDBProviderFactory INSTANCE = new LevelDBProviderFactory();

    @Override
    public LevelProvider create(String levelId, Path levelsPath, Executor executor) throws IOException {
        return new LevelDBProvider(levelId, levelsPath, executor);
    }

    @Override
    public boolean isCompatible(String levelId, Path levelsPath) {
        Path levelPath = levelsPath.resolve(levelId);
        if (Files.isDirectory(levelPath)) {
            Path regionPath = levelPath.resolve("region");
            if (Files.isDirectory(regionPath)) {
                Visitor visitor = new Visitor();
                try {
                    Files.walkFileTree(regionPath, visitor);
                    return visitor.found;
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return false;
    }

    private static class Visitor extends SimpleFileVisitor<Path> {
        private static final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.ldb");

        private boolean found;

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
            if (matcher.matches(path)) {
                found = true;
                return FileVisitResult.TERMINATE;
            }
            return FileVisitResult.CONTINUE;
        }
    }
}

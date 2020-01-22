package cn.nukkit.level.provider.leveldb;

import cn.nukkit.level.provider.LevelProvider;
import cn.nukkit.level.provider.LevelProviderFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Executor;

@Log4j2
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
            Path dbPath = levelPath.resolve("db");
            if (Files.isDirectory(dbPath)) {
                Visitor visitor = new Visitor();
                try {
                    Files.walkFileTree(dbPath, visitor);
                    return visitor.found;
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return false;
    }

    private static class Visitor extends SimpleFileVisitor<Path> {
        private static final PathMatcher PATH_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.ldb");

        private boolean found;

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
            if (PATH_MATCHER.matches(path)) {
                found = true;
                return FileVisitResult.TERMINATE;
            }
            return FileVisitResult.CONTINUE;
        }
    }
}

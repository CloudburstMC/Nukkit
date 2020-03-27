package cn.nukkit.level.provider.anvil;

import cn.nukkit.level.LevelConverter;
import cn.nukkit.level.provider.LevelProvider;
import cn.nukkit.level.provider.LevelProviderFactory;
import cn.nukkit.level.provider.leveldb.LevelDBProviderFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnvilProviderFactory implements LevelProviderFactory {

    public static final AnvilProviderFactory INSTANCE = new AnvilProviderFactory();

    @Override
    public LevelProvider create(String levelId, Path levelsPath, Executor executor) throws IOException {
        try (LevelProvider oldProvider = new AnvilProvider(levelId, levelsPath, executor);
             LevelProvider newProvider = LevelDBProviderFactory.INSTANCE.create(levelId, levelsPath, executor)) {

            LevelConverter converter = new LevelConverter(oldProvider, newProvider);
            converter.perform().join();
        }

        try (Stream<Path> walk = Files.walk(levelsPath.resolve(levelId).resolve("region"))) {
            for (Path path : walk.sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not delete region/ directory");
        }

        return LevelDBProviderFactory.INSTANCE.create(levelId, levelsPath, executor);
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
        private static final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.mca");

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

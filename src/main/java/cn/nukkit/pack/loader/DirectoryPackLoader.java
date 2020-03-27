package cn.nukkit.pack.loader;

import cn.nukkit.utils.Utils;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Log4j2
@ToString(exclude = {"networkPreparedFuture"})
public class DirectoryPackLoader implements PackLoader {
    public static final DirectoryFactory FACTORY = new DirectoryFactory();

    private static final List<Path> TEMP_FILES = new ArrayList<>();

    static {
        Utils.addShutdownTask(() -> {
            for (Path path : TEMP_FILES) {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                }
            }
        });
    }

    private final Path path;

    private transient CompletableFuture<Path> networkPreparedFuture;

    private DirectoryPackLoader(Path path) {
        this.path = path;
    }

    @Override
    public Path getLocation() {
        return path;
    }

    @Override
    public boolean hasAsset(Path path) {
        Path asset = this.path.resolve(path);
        return Files.exists(asset) && Files.isRegularFile(asset);
    }

    @Override
    public InputStream getAsset(Path path) throws IOException {
        Path asset = this.path.resolve(path);
        return Files.newInputStream(asset);
    }

    @Override
    public boolean hasFolder(Path path) {
        Path folder = this.path.resolve(path);
        return Files.exists(folder) && Files.isDirectory(folder);
    }

    @Override
    public void forEachIn(Path path, Consumer<Path> consumer, boolean recurse) {
        Path resolved = this.path.resolve(path);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(resolved)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry) && recurse) {
                    forEachIn(entry, consumer, true);
                } else {
                    consumer.accept(this.path.relativize(entry));
                }
            }
        } catch (IOException e) {
            // ignore
        }
    }

    @Override
    public CompletableFuture<Path> getNetworkPreparedFile() {
        if (networkPreparedFuture == null) {
            networkPreparedFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    Path temp = Files.createTempFile("nukkit-pack", String.valueOf(System.currentTimeMillis()));
                    try (OutputStream out = Files.newOutputStream(temp); ZipOutputStream zipOut = new ZipOutputStream(out)) {
                        this.forEachIn(Paths.get(""), entry -> {
                            try {
                                ZipEntry zipEntry = new ZipEntry(entry.toString());
                                zipOut.putNextEntry(zipEntry);
                                Files.copy(entry, zipOut);
                                zipOut.closeEntry();
                            } catch (IOException ignored) {
                            }
                        }, true);
                    }
                    TEMP_FILES.add(temp);
                    return temp;
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            });
        }
        return networkPreparedFuture;
    }

    @Override
    public void close() throws IOException {
        // no-op
    }

    private static class DirectoryFactory implements Factory {

        private DirectoryFactory() {
        }

        @Override
        public boolean canLoad(Path path) {
            return Files.isDirectory(path);
        }

        @Override
        public PackLoader create(Path path) throws IOException {
            return new DirectoryPackLoader(path);
        }
    }
}

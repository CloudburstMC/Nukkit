package cn.nukkit.pack.loader;

import lombok.ToString;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@ToString(exclude = {"zipFile", "networkPreparedFuture"})
public class ZipPackLoader implements PackLoader {
    public static final PackLoader.Factory FACTORY = new ZipFactory();

    private final Path path;
    private final ZipFile zipFile;

    private transient CompletableFuture<Path> networkPreparedFuture;

    private ZipPackLoader(Path path) throws IOException {
        this.path = path;
        try {
            this.zipFile = new ZipFile(this.path.toFile());
        } catch (IOException e) {
            throw new IOException("Path is not a zip file", e);
        }
    }

    private ZipEntry getEntry(Path path) {
        return this.zipFile.getEntry(path.toString());
    }

    @Override
    public Path getLocation() {
        return path;
    }

    @Override
    public boolean hasAsset(Path path) {
        ZipEntry entry = getEntry(path);
        return entry != null && !entry.isDirectory();
    }

    @Nullable
    @Override
    public InputStream getAsset(Path path) throws IOException {
        ZipEntry entry = getEntry(path);
        if (entry == null) {
            return null;
        }
        return this.zipFile.getInputStream(entry);
    }

    @Override
    public boolean hasFolder(Path folder) {
        ZipEntry entry = getEntry(path);
        return entry != null && entry.isDirectory();
    }

    @Override
    public void forEachIn(Path path, Consumer<Path> consumer, boolean recurse) {
        Enumeration<? extends ZipEntry> entries = this.zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();

            Path entryPath = Paths.get(entry.getName());
            if (entryPath.getParent().equals(path)) {
                consumer.accept(entryPath);
            }
            if (entry.isDirectory()) {
                forEachIn(entryPath, consumer, true);
            }
        }
    }

    @Override
    public CompletableFuture<Path> getNetworkPreparedFile() {
        if (networkPreparedFuture == null) {
            networkPreparedFuture = CompletableFuture.completedFuture(path);
        }
        return networkPreparedFuture;
    }

    @Override
    public void close() throws IOException {
        this.zipFile.close();
    }

    private static class ZipFactory implements Factory {
        private static final PathMatcher MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.{zip,mcpack}");

        private ZipFactory() {
        }

        @Override
        public boolean canLoad(Path path) {
            return MATCHER.matches(path);
        }

        @Override
        public PackLoader create(Path path) throws IOException {
            return new ZipPackLoader(path);
        }
    }
}

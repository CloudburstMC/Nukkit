package cn.nukkit.pack.loader;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface PackLoader extends Closeable {

    Path getLocation();

    boolean hasAsset(Path path);

    InputStream getAsset(Path path) throws IOException;

    boolean hasFolder(Path folder);

    void forEachIn(Path path, Consumer<Path> consumer, boolean recurse);

    CompletableFuture<Path> getNetworkPreparedFile();

    interface Factory {

        boolean canLoad(Path path);

        PackLoader create(Path path) throws IOException;
    }
}

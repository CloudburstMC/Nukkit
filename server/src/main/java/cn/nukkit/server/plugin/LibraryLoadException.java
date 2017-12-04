package cn.nukkit.server.plugin;

/**
 * Created on 15-12-13.
 */
public class LibraryLoadException extends RuntimeException {

    public LibraryLoadException(Library library) {
        super("Load library " + library.getArtifactId() + " failed!");
    }

}

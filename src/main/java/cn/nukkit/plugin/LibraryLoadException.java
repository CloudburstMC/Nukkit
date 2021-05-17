package cn.nukkit.plugin;

/**
 * @since 15-12-13
 */
public class LibraryLoadException extends RuntimeException {

    public LibraryLoadException(Library library) {
        super("Load library " + library.getArtifactId() + " failed!");
    }

}

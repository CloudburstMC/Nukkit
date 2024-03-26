package cn.nukkit.plugin;

@SuppressWarnings("serial")
public class LibraryLoadException extends RuntimeException {

    public LibraryLoadException(Library library) {
        super("Load library " + library.getArtifactId() + " failed!");
    }
}

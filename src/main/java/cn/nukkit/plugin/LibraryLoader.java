package cn.nukkit.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.logging.Logger;

/**
 * Created on 15-12-13.
 */
public class LibraryLoader {

    private static final File BASE_FOLDER = new File("./libraries");
    private static final Logger LOGGER = Logger.getLogger("LibraryLoader");
    private static final String SUFFIX = ".jar";

    static {
        if (BASE_FOLDER.mkdir()) {
            LOGGER.info("Created libraries folder.");
        }
    }

    public static void load(String library) {
        String[] split = library.split(":");
        if (split.length != 3) {
            throw new IllegalArgumentException(library);
        }
        load(new Library() {
            public String getGroupId() {
                return split[0];
            }

            public String getArtifactId() {
                return split[1];
            }

            public String getVersion() {
                return split[2];
            }
        });
    }

    public static void load(Library library) {
        String filePath = library.getGroupId().replace('.', '/') + '/' + library.getArtifactId() + '/' + library.getVersion();
        String fileName = library.getArtifactId() + '-' + library.getVersion() + SUFFIX;

        File folder = new File(BASE_FOLDER, filePath);
        if (folder.mkdirs()) {
            LOGGER.info("Created " + folder.getPath() + '.');
        }

        File file = new File(folder, fileName);
        if (!file.isFile()) try {
            URL url = new URL("https://repo1.maven.org/maven2/" + filePath + '/' + fileName);
            LOGGER.info("Get library from " + url + '.');
            Files.copy(url.openStream(), file.toPath());
            LOGGER.info("Get library " + fileName + " done!");
        } catch (IOException e) {
            throw new LibraryLoadException(library);
        }

        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            boolean accessible = method.isAccessible();
            if (!accessible) {
                method.setAccessible(true);
            }
            URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
            URL url = file.toURI().toURL();
            method.invoke(classLoader, url);
            method.setAccessible(accessible);
        } catch (NoSuchMethodException | MalformedURLException | IllegalAccessException | InvocationTargetException e) {
            throw new LibraryLoadException(library);
        }

        LOGGER.info("Load library " + fileName + " done!");
    }

    public static File getBaseFolder() {
        return BASE_FOLDER;
    }

}

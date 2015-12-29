package cn.nukkit.plugin;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created on 15-12-13.
 */
public class LibraryLoaderTest {

    @Test
    public void testLoad() {
        TestLibrary library = new TestLibrary();
        LibraryLoader.load(library);
        try {
            Class<?> loadedClass = getClass().getClassLoader().loadClass("joptsimple.OptionParser");
            Assert.assertNotNull(loadedClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        delete(LibraryLoader.getBaseFolder());
    }

    private void delete(File file) {
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                delete(f);
            } else {
                f.delete();
            }
        }
        file.delete();
    }

    class TestLibrary implements Library {

        @Override
        public String getGroupId() {
            return "net.sf.jopt-simple";
        }

        @Override
        public String getArtifactId() {
            return "jopt-simple";
        }

        @Override
        public String getVersion() {
            return "4.9";
        }
    }

}

package cn.nukkit.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class JarClassLoader extends URLClassLoader {
    public JarClassLoader(ClassLoader parent, File file) throws MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, parent);
    }
}

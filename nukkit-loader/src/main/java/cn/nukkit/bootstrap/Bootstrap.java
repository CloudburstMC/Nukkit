package cn.nukkit.bootstrap;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;

/**
 * Nukkit startup class
 * This class contains codes to boot the server
 *
 * Note: Plugin developers may not include this module or use any methods in this package in any form
 */
public class Bootstrap {
    public static final String NUKKIT_CLASS_STRING = "cn.nukkit.Nukkit";
    public static final String NUKKIT_UI_CLASS_STRING = "";

    public static ClassLoader BOOTSTRAP_CLASSLOADER = Bootstrap.class.getClassLoader();
    public static Class NUKKIT_CLASS = null;
    public static Class NUKKIT_UI_CLASS = null;

    public static int STATUS_CODE = 0;

    public static void main(String args[]){
        try{
            System.out.println("Loading libraries...");

            //Startup checks
            ArgumentsParser parser = new ArgumentsParser(args);
            Options startupOptions = parser.getOptions();

            String systemName = System.getProperty("os.name").toLowerCase();
            if (systemName.contains("windows")) {
                if (systemName.contains("windows 8") || systemName.contains("2012")) {
                    startupOptions.SHORT_TITLE = true;
                }
            }

            //Override some options
            parser.parseArguments();

            //Load classes, trying to search libraries
            File bootstrapClasspath = new File(Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File librariesDirectory = null;

            try{
                File hint = new File(bootstrapClasspath, "lib");
                if(hint.exists()){
                    librariesDirectory = hint;
                }
            }catch (Exception ignore){}

            try{
                File hint = bootstrapClasspath.getParentFile();
                if(hint.listFiles((file) -> !file.equals(bootstrapClasspath) && file.getName().endsWith(".jar") || file.getName().endsWith(".zip")).length > 0){
                    librariesDirectory = hint;
                }
            }catch (Exception ignore){}

            try{
                File hint = new File(bootstrapClasspath.getParentFile(), "lib");
                if(hint.exists()){
                    librariesDirectory = hint;
                }
            }catch (Exception ignore){}

            if(librariesDirectory != null){
                Bootstrap.BOOTSTRAP_CLASSLOADER = Bootstrap.loadLibraries(librariesDirectory);
            }

            try{
                Bootstrap.NUKKIT_CLASS = Bootstrap.BOOTSTRAP_CLASSLOADER.loadClass(Bootstrap.NUKKIT_CLASS_STRING);
                Bootstrap.NUKKIT_UI_CLASS = Bootstrap.BOOTSTRAP_CLASSLOADER.loadClass(Bootstrap.NUKKIT_UI_CLASS_STRING);
            }catch (Exception ignore){}

            //Initialize
            Bootstrap.initializeNukkit(startupOptions);
        }catch (Throwable e){
            if(e instanceof InvocationTargetException){
                e = ((InvocationTargetException) e).getTargetException();
            }
            System.out.println("Unable to load Nukkit: " + e);
            e.printStackTrace();
            Bootstrap.STATUS_CODE |= 0b01;
        }

        try{
            if((Bootstrap.STATUS_CODE & 0b01) == 0){
                Bootstrap.runServer();
            }
        }catch (Throwable t){
            if(t instanceof InvocationTargetException){
                t = ((InvocationTargetException) t).getTargetException();
            }
            System.out.println("Server had interrupted by an exception which has been caught.");
            System.out.println(t.toString());
            t.printStackTrace(System.out);
            Bootstrap.STATUS_CODE |= 0b10;
        }

        try{
            if((Bootstrap.STATUS_CODE & 0b01) == 0){
                Bootstrap.shutdownServer();
            }
        }catch (Throwable t){
            if(t instanceof InvocationTargetException){
                t = ((InvocationTargetException) t).getTargetException();
            }
            System.out.println("Exception caught while shutting down the server.");
            System.out.println(t.toString());
            t.printStackTrace(System.out);
            Bootstrap.STATUS_CODE |= 0b0100;
        }

        System.exit(Bootstrap.STATUS_CODE);
    }

    public static URLClassLoader loadLibraries(File librariesDirectory) throws MalformedURLException {
        File[] libraries = librariesDirectory.listFiles((dir, name) -> name.endsWith(".jar") || name.endsWith(".zip"));
        URL[] librariesUrl = new URL[libraries.length];
        for(int i = 0; i < libraries.length; ++i){
            librariesUrl[i] = libraries[i].toURI().toURL();
        }
        return new URLClassLoader(librariesUrl);
    }

    private static void shutdownServer() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> nukkitClass = Objects.requireNonNull(Bootstrap.NUKKIT_CLASS);

        Method startupMethod = nukkitClass.getDeclaredMethod("interruptServer");
        startupMethod.invoke(null);
    }

    private static void runServer() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> nukkitClass = Objects.requireNonNull(Bootstrap.NUKKIT_CLASS);

        Method startupMethod = nukkitClass.getDeclaredMethod("startupServer");
        startupMethod.invoke(null);
    }

    private static void initializeNukkit(Options options) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> initializeClass = Objects.requireNonNull(Bootstrap.NUKKIT_CLASS);

        Method initializeOptionsMethod = initializeClass.getDeclaredMethod("setOptions", Options.class);
        initializeOptionsMethod.invoke(null, options);

        Method initializeMethod = initializeClass.getDeclaredMethod("initializeNukkit");
        initializeMethod.invoke(null);
    }
}

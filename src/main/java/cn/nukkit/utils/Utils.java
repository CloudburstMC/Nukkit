package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
public class Utils {
    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public static final Integer[] EMPTY_INTEGERS = new Integer[0];

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public static final SplittableRandom random = new SplittableRandom();
    
    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public static void safeWrite(File currentFile, Consumer<File> operation) throws IOException {
        File parent = currentFile.getParentFile();
        File newFile = new File(parent, currentFile.getName()+"_new");
        File oldFile = new File(parent, currentFile.getName()+"_old");
        File olderFile = new File(parent, currentFile.getName()+"_older");

        if (olderFile.isFile() && !olderFile.delete()) {
            log.fatal("Could not delete the file "+olderFile.getAbsolutePath());
        }

        if (newFile.isFile() && !newFile.delete()) {
            log.fatal("Could not delete the file "+newFile.getAbsolutePath());
        }
        
        try {
            operation.accept(newFile);
        } catch (Exception e) {
            throw new IOException(e);
        }
        
        if (oldFile.isFile()) {
            if (olderFile.isFile()) {
                Utils.copyFile(oldFile, olderFile);
            } else if (!oldFile.renameTo(olderFile)) {
                throw new IOException("Could not rename the " + oldFile + " to " + olderFile);
            }
        }

        if (currentFile.isFile() && !currentFile.renameTo(oldFile)) {
            throw new IOException("Could not rename the " + currentFile + " to " + oldFile);
        }

        if (!newFile.renameTo(currentFile)) {
            throw new IOException("Could not rename the " + newFile + " to " + currentFile);
        }
    }

    public static void writeFile(String fileName, String content) throws IOException {
        writeFile(fileName, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    public static void writeFile(String fileName, InputStream content) throws IOException {
        writeFile(new File(fileName), content);
    }

    public static void writeFile(File file, String content) throws IOException {
        writeFile(file, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    public static void writeFile(File file, InputStream content) throws IOException {
        if (content == null) {
            throw new IllegalArgumentException("content must not be null");
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        try (FileOutputStream stream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = content.read(buffer)) != -1) {
                stream.write(buffer, 0, length);
            }
        }
        content.close();
    }

    public static String readFile(File file) throws IOException {
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException();
        }
        return readFile(new FileInputStream(file));
    }

    public static String readFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException();
        }
        return readFile(new FileInputStream(file));
    }

    public static String readFile(InputStream inputStream) throws IOException {
        return readFile(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    private static String readFile(Reader reader) throws IOException {
        try (BufferedReader br = new BufferedReader(reader)) {
            String temp;
            StringBuilder stringBuilder = new StringBuilder();
            temp = br.readLine();
            while (temp != null) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("\n");
                }
                stringBuilder.append(temp);
                temp = br.readLine();
            }
            return stringBuilder.toString();
        }
    }

    public static void copyFile(File from, File to) throws IOException {
        if (!from.exists()) {
            throw new FileNotFoundException();
        }
        if (from.isDirectory() || to.isDirectory()) {
            throw new FileNotFoundException();
        }
        FileInputStream fi = null;
        FileChannel in = null;
        FileOutputStream fo = null;
        FileChannel out = null;
        try {
            if (!to.exists()) {
                to.createNewFile();
            }
            fi = new FileInputStream(from);
            in = fi.getChannel();
            fo = new FileOutputStream(to);
            out = fo.getChannel();
            in.transferTo(0, in.size(), out);
        } finally {
            if (fi != null) fi.close();
            if (in != null) in.close();
            if (fo != null) fo.close();
            if (out != null) out.close();
        }
    }

    public static String getAllThreadDumps() {
        ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
        StringBuilder builder = new StringBuilder();
        for (ThreadInfo info : threads) {
            builder.append('\n').append(info);
        }
        return builder.toString();
    }


    public static String getExceptionMessage(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            e.printStackTrace(printWriter);
            printWriter.flush();
        }
        return stringWriter.toString();
    }

    public static UUID dataToUUID(String... params) {
        StringBuilder builder = new StringBuilder();
        for (String param : params) {
            builder.append(param);
        }
        return UUID.nameUUIDFromBytes(builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static UUID dataToUUID(byte[]... params) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        for (byte[] param : params) {
            try {
                stream.write(param);
            } catch (IOException e) {
                break;
            }
        }
        return UUID.nameUUIDFromBytes(stream.toByteArray());
    }

    public static String rtrim(String s, char character) {
        int i = s.length() - 1;
        while (i >= 0 && (s.charAt(i)) == character) {
            i--;
        }
        return s.substring(0, i + 1);
    }

    public static boolean isByteArrayEmpty(final byte[] array) {
        for (byte b : array) {
            if (b != 0) {
                return false;
            }
        }
        return true;
    }

    public static long toRGB(byte r, byte g, byte b, byte a) {
        long result = (int) r & 0xff;
        result |= ((int) g & 0xff) << 8;
        result |= ((int) b & 0xff) << 16;
        result |= ((int) a & 0xff) << 24;
        return result & 0xFFFFFFFFL;
    }

    public static long toABGR(int argb) {
        long result = argb & 0xFF00FF00L;
        result |= (argb << 16) & 0x00FF0000L; // B to R
        result |= (argb >>> 16) & 0xFFL; // R to B
        return result & 0xFFFFFFFFL;
    }

    public static Object[][] splitArray(Object[] arrayToSplit, int chunkSize) {
        if (chunkSize <= 0) {
            return null;
        }

        int rest = arrayToSplit.length % chunkSize;
        int chunks = arrayToSplit.length / chunkSize + (rest > 0 ? 1 : 0);

        Object[][] arrays = new Object[chunks][];
        for (int i = 0; i < (rest > 0 ? chunks - 1 : chunks); i++) {
            arrays[i] = Arrays.copyOfRange(arrayToSplit, i * chunkSize, i * chunkSize + chunkSize);
        }
        if (rest > 0) {
            arrays[chunks - 1] = Arrays.copyOfRange(arrayToSplit, (chunks - 1) * chunkSize, (chunks - 1) * chunkSize + rest);
        }
        return arrays;
    }

    public static <T> void reverseArray(T[] data) {
        reverseArray(data, false);
    }

    public static <T> T[] reverseArray(T[] array, boolean copy) {
        T[] data = array;

        if (copy) {
            data = Arrays.copyOf(array, array.length);
        }

        for (int left = 0, right = data.length - 1; left < right; left++, right--) {
            // swap the values at the left and right indices
            T temp = data[left];
            data[left] = data[right];
            data[right] = temp;
        }

        return data;
    }

    public static <T> T[][] clone2dArray(T[][] array) {
        T[][] newArray = Arrays.copyOf(array, array.length);

        for (int i = 0; i < array.length; i++) {
            newArray[i] = Arrays.copyOf(array[i], array[i].length);
        }

        return newArray;
    }

    public static <T,U,V> Map<U,V> getOrCreate(Map<T, Map<U, V>> map, T key) {
        Map<U, V> existing = map.get(key);
        if (existing == null) {
            ConcurrentHashMap<U, V> toPut = new ConcurrentHashMap<>();
            existing = map.putIfAbsent(key, toPut);
            if (existing == null) {
                existing = toPut;
            }
        }
        return existing;
    }

    public static <T, U, V extends U> U getOrCreate(Map<T, U> map, Class<V> clazz, T key) {
        U existing = map.get(key);
        if (existing != null) {
            return existing;
        }
        try {
            U toPut = clazz.newInstance();
            existing = map.putIfAbsent(key, toPut);
            if (existing == null) {
                return toPut;
            }
            return existing;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static int toInt(Object number) {
        if (number instanceof Integer) {
            return (Integer) number;
        }

        return (int) Math.round((double) number);
    }

    public static byte[] parseHexBinary(String s) {
        final int len = s.length();

        // "111" is not a valid hex encoding.
        if(len % 2 != 0)
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);

        byte[] out = new byte[len / 2];

        for(int i = 0; i < len; i += 2) {
            int h = hexToBin(s.charAt(i));
            int l = hexToBin(s.charAt(i + 1));
            if(h == -1 || l == -1)
                throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);

            out[i / 2] = (byte)(h * 16 + l);
        }

        return out;
    }

    private static int hexToBin( char ch ) {
        if('0' <= ch && ch <= '9')    return ch - '0';
        if('A' <= ch && ch <= 'F')    return ch - 'A' + 10;
        if('a' <= ch && ch <= 'f')    return ch - 'a' + 10;
        return -1;
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public static int rand(int min, int max) {
        if (min == max) {
            return max;
        }
        return random.nextInt(max + 1 - min) + min;
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public static double rand(double min, double max) {
        if (min == max) {
            return max;
        }
        return min + random.nextDouble() * (max-min);
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public static boolean rand() {
        return random.nextBoolean();
    }

    /**
     * A way to tell the java compiler to do not replace the users of a {@code public static final int} constant
     * with the value defined in it, forcing the JVM to get the value directly from the class, preventing
     * binary incompatible changes.
     * @see <a href="https://stackoverflow.com/a/12065326/804976>https://stackoverflow.com/a/12065326/804976</a>
     * @param value The value to be assigned to the field.
     * @return The same value that was passed as parameter
     */
    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public static int dynamic(int value) {
        return value;
    }
}

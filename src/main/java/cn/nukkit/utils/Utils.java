package cn.nukkit.utils;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Utils {

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
        FileOutputStream stream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = content.read(buffer)) != -1) {
            stream.write(buffer, 0, length);
        }
        stream.close();
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
        BufferedReader br = new BufferedReader(reader);
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
        br.close();
        reader.close();
        return stringBuilder.toString();
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
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
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

    public static void reverseArray(Object[] data) {
        reverseArray(data, false);
    }

    public static Object[] reverseArray(Object[] array, boolean copy) {
        Object[] data = array;
        if (copy) {
            data = new Object[array.length];
            System.arraycopy(array, 0, data, 0, data.length);
        }

        for (int left = 0, right = data.length - 1; left < right; left++, right--) {
            // swap the values at the left and right indices
            Object temp = data[left];
            data[left] = data[right];
            data[right] = temp;
        }

        return data;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[][] clone2dArray(T[][] array) {
        T[][] newArray = (T[][]) new Object[array.length][];
        for (int i = 0; i < newArray.length; i++) {
            T[] old = array[i];
            T[] n = (T[]) new Object[old.length];

            System.arraycopy(old, 0, n, 0, n.length);
            newArray[i] = n;
        }

        return newArray;
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
}

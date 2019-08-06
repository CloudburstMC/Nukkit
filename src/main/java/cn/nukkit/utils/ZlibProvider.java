package cn.nukkit.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author ScraMTeam
 */
interface ZlibProvider {
    byte[] deflate(byte[][] data, int level) throws Exception;

    byte[] deflate(byte[] data, int level) throws Exception;

    byte[] inflate(InputStream stream) throws IOException;
}

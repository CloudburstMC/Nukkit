package cn.nukkit.utils;

import java.io.IOException;

/**
 *
 * @author ScraMTeam
 */
interface ZlibProvider {
    byte[] deflate(byte[][] data, int level) throws IOException;

    byte[] deflate(byte[] data, int level) throws IOException;

    byte[] inflate(byte[] data, int maxSize) throws IOException;
}

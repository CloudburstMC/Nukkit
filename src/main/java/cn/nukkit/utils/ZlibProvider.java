package cn.nukkit.utils;

import java.io.IOException;

/**
 * ZlibProvider
 *
 * @author ScraMTeam
 */
interface ZlibProvider {

    byte[] deflate(byte[][] data, int level) throws IOException;

    byte[] deflate(byte[] data, int level) throws IOException;

    byte[] deflateRaw(byte[][] data, int level) throws IOException;

    byte[] deflateRaw(byte[] data, int level) throws IOException;

    byte[] inflate(byte[] data, int maxSize) throws IOException;

    byte[] inflateRaw(byte[] data, int maxSize) throws IOException;
}

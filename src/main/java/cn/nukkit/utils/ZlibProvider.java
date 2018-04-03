package cn.nukkit.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author ScraMTeam
 */
interface ZlibProvider {
    public byte[] deflate(byte[][] data, int level) throws Exception;
    public byte[] deflate(byte[] data, int level) throws Exception;
    public byte[] inflate(InputStream stream) throws IOException;
}

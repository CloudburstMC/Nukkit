package cn.nukkit.server.test;

import cn.nukkit.server.utils.Zlib;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Zlib")
class ZlibTest {

    @DisplayName("Inflate and Deflate")
    @Test
    void testAll() throws Exception {
        byte[] in = "lmlstarqaq".getBytes();
        byte[] compressed = Zlib.deflate(in);
        byte[] out = Zlib.inflate(compressed);
        assertTrue(Arrays.equals(in, out));
    }

}

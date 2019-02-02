package cn.nukkit.test;

import cn.nukkit.utils.Zlib;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@DisplayName("Zlib")
class ZlibTest {

    @DisplayName("Inflate and Deflate")
    @Test
    void testAll() throws Exception {
        byte[] in = "lmlstarqaq".getBytes();
        byte[] compressed = Zlib.deflate(in);
        byte[] out = Zlib.inflate(compressed);
        assertArrayEquals(in, out);
    }

}

package cn.nukkit.utils;

import cn.nukkit.nbt.stream.FastByteArrayOutputStream;

public class ThreadCache {
    public static void clean() {
        idArray.clean();
        dataArray.clean();
        byteCache6144.clean();
        boolCache4096.clean();
        charCache4096.clean();
        charCache4096v2.clean();
        fbaos.clean();
        binaryStream.clean();
        intCache256.clean();
        byteCache256.clean();
        ZlibThreadLocal.def.clean();
        ZlibThreadLocal.buf.clean();
    }

    public static final IterableThreadLocal<byte[][]> idArray = new IterableThreadLocal<byte[][]>() {
        @Override
        public byte[][] init() {
            return new byte[16][];
        }
    };

    public static final IterableThreadLocal<byte[][]> dataArray = new IterableThreadLocal<byte[][]>() {
        @Override
        public byte[][] init() {
            return new byte[16][];
        }
    };

    public static final IterableThreadLocal<byte[]> byteCache6144 = new IterableThreadLocal<byte[]>() {
        @Override
        public byte[] init() {
            return new byte[6144];
        }
    };

    public static final IterableThreadLocal<byte[]> byteCache256 = new IterableThreadLocal<byte[]>() {
        @Override
        public byte[] init() {
            return new byte[256];
        }
    };

    public static final IterableThreadLocal<boolean[]> boolCache4096 = new IterableThreadLocal<boolean[]>() {
        @Override
        public boolean[] init() {
            return new boolean[4096];
        }
    };

    public static final IterableThreadLocal<char[]> charCache4096v2 = new IterableThreadLocal<char[]>() {
        @Override
        public char[] init() {
            return new char[4096];
        }
    };

    public static final IterableThreadLocal<char[]> charCache4096 = new IterableThreadLocal<char[]>() {
        @Override
        public char[] init() {
            return new char[4096];
        }
    };

    public static final IterableThreadLocal<int[]> intCache256 = new IterableThreadLocal<int[]>() {
        @Override
        public int[] init() {
            return new int[256];
        }
    };

    public static final IterableThreadLocal<FastByteArrayOutputStream> fbaos = new IterableThreadLocal<FastByteArrayOutputStream>() {
        @Override
        public FastByteArrayOutputStream init() {
            return new FastByteArrayOutputStream(1024);
        }
    };

    public static final IterableThreadLocal<BinaryStream> binaryStream = new IterableThreadLocal<BinaryStream>() {
        @Override
        public BinaryStream init() {
            return new BinaryStream();
        }
    };
}

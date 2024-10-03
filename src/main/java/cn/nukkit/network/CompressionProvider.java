package cn.nukkit.network;

import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.SnappyCompression;
import cn.nukkit.utils.Zlib;

public interface CompressionProvider {

    CompressionProvider NONE = new CompressionProvider() {
        @Override
        public byte[] compress(BinaryStream packet, int level) throws Exception {
            return packet.getBuffer();
        }

        @Override
        public byte[] decompress(byte[] compressed) throws Exception {
            return compressed;
        }

        @Override
        public byte[] decompress(byte[] compressed, int maxSize) throws Exception {
            return compressed;
        }

        @Override
        public byte getPrefix() {
            return (byte) 0xff;
        }
    };

    CompressionProvider ZLIB_RAW = new CompressionProvider() {
        @Override
        public byte[] compress(BinaryStream packet, int level) throws Exception {
            return Zlib.deflateRaw(packet.getBuffer(), level);
        }

        @Override
        public byte[] decompress(byte[] compressed) throws Exception {
            return Zlib.inflateRaw(compressed, 6291456);
        }

        @Override
        public byte[] decompress(byte[] compressed, int maxSize) throws Exception {
            return Zlib.inflateRaw(compressed, maxSize);
        }

        @Override
        public byte getPrefix() {
            return (byte) 0x00;
        }
    };

    CompressionProvider SNAPPY = new CompressionProvider() {
        @Override
        public byte[] compress(BinaryStream packet, int level) throws Exception {
            return SnappyCompression.compress(packet.getBuffer());
        }

        @Override
        public byte[] decompress(byte[] compressed) throws Exception {
            return SnappyCompression.decompress(compressed, 6291456);
        }

        @Override
        public byte[] decompress(byte[] compressed, int maxSize) throws Exception {
            return SnappyCompression.decompress(compressed, maxSize);
        }

        @Override
        public byte getPrefix() {
            return (byte) 0x01;
        }
    };


    byte[] compress(BinaryStream packet, int level) throws Exception;
    byte[] decompress(byte[] compressed) throws Exception;

    default byte[] decompress(byte[] compressed, int maxSize) throws Exception {
        return this.decompress(compressed);
    }

    static CompressionProvider from(PacketCompressionAlgorithm algorithm) {
        if (algorithm == null) {
            return NONE;
        } else if (algorithm == PacketCompressionAlgorithm.ZLIB) {
            return ZLIB_RAW;
        } else if (algorithm == PacketCompressionAlgorithm.SNAPPY) {
            return SNAPPY;
        }
        throw new UnsupportedOperationException();
    }

    default byte getPrefix() {
        throw new UnsupportedOperationException();
    }

    static CompressionProvider byPrefix(byte prefix) {
        switch (prefix) {
            case 0x00:
                return ZLIB_RAW;
            case 0x01:
                return SNAPPY;
            case (byte) 0xff:
                return NONE;
        }
        throw new IllegalArgumentException("Unknown compression type: " + prefix);
    }
}

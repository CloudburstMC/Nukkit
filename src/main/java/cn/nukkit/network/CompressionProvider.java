package cn.nukkit.network;

import cn.nukkit.network.protocol.types.PacketCompressionAlgorithm;
import cn.nukkit.utils.BinaryStream;

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
        public byte getPrefix() {
            return (byte) 0xff;
        }
    };

    CompressionProvider ZLIB = new CompressionProvider() {
        @Override
        public byte[] compress(BinaryStream packet, int level) throws Exception {
            return Network.deflateRaw(packet.getBuffer(), level);
        }

        @Override
        public byte[] decompress(byte[] compressed) throws Exception {
            return Network.inflateRaw(compressed);
        }

        @Override
        public byte getPrefix() {
            return (byte) 0x00;
        }
    };


    byte[] compress(BinaryStream packet, int level) throws Exception;
    byte[] decompress(byte[] compressed) throws Exception;

    static CompressionProvider from(PacketCompressionAlgorithm algorithm) {
        if (algorithm == null) {
            return NONE;
        } else if (algorithm == PacketCompressionAlgorithm.ZLIB) {
            return ZLIB;
        }
        throw new UnsupportedOperationException();
    }

    default byte getPrefix() {
        throw new UnsupportedOperationException();
    }

    static CompressionProvider byPrefix(byte prefix) {
        switch (prefix) {
            case 0x00:
                return ZLIB;
            case (byte) 0xff:
                return NONE;
        }
        throw new IllegalArgumentException("Unsupported compression type: " + prefix);
    }
}

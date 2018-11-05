package com.nukkitx.server.level.util;

import com.flowpowered.math.GenericMath;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public class Palette {
    private final Version version;

    public Palette(int version, boolean read) {
        this.version = Version.getVersion(version, read);
    }

    public void writeIndexes(ByteBuf buf, short[] indexes) {
        int bits = 0;
        int bitSetOffset = 0;
        int wordsWritten = 0;

        for (int index : indexes) {
            if (wordsWritten == version.blocksPerWord) {
                buf.writeIntLE(bits);

                bits = 0;
                bitSetOffset = 0;
                wordsWritten = 0;
            }

            while (index != 0L) {
                if ((index % 2L) != 0) {
                    bits |= (1 << bitSetOffset);
                }

                bitSetOffset++;
                index = index >>> 1;
            }

            wordsWritten++;

            bitSetOffset = wordsWritten * version.id;
        }
        buf.writeIntLE(bits);
    }

    public short[] readIndexes() {
        return null; //TODO
    }

    public enum Version {
        V1(1, 32),
        V2(2, 16),
        V3(3, 10, 2),
        V4(4, 8),
        V5(5, 6, 2),
        V6(6, 5, 2),
        V8(8, 4),
        V16(16, 2);

        @Getter
        private final byte id;
        private final byte blocksPerWord;
        private final byte padding;

        Version(int version, int blocksPerWord) {
            this(version, blocksPerWord, 0 );
        }

        Version(int id, int blocksPerWord, int padding) {
            this.id = (byte) id;
            this.blocksPerWord = (byte) blocksPerWord;
            this.padding = (byte) padding;
        }

        private static Version getVersion(int version, boolean read) {
            for (Version ver : values()) {
                if ( ( !read && ver.blocksPerWord <= version ) || ( read && ver.id == version ) ) {
                    return ver;
                }
            }
            throw new IllegalArgumentException("Invalid palette version: " + version);
        }
    }

    public static boolean isRuntime(int paletteInfo) {
        return (paletteInfo & 1) != 0;
    }

    public static int getVersion(int paletteInfo) {
        return paletteInfo >> 1;
    }

    public static int getPalletInfo(Version version, boolean runtime) {
        return (version.id << 1) | (runtime ? 1 : 0);
    }

    public static int getBlocksPerWord(int paletteLength) {
        int numberOfBits = log2(paletteLength) + 1;
        return GenericMath.floor(32f / (float) numberOfBits);
    }

    private static int log2(int val) {
        return 31 - Integer.numberOfLeadingZeros(val);
    }
}

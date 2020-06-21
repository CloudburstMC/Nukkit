package cn.nukkit.level.format.anvil.palette;

import cn.nukkit.math.MathHelper;
import cn.nukkit.utils.ThreadCache;
import java.util.Arrays;

@Deprecated
public final class BiomePalette {

    private int biome;

    private BitArray256 encodedData;

    private IntPalette palette;

    private BiomePalette(final BiomePalette clone) {
        this.biome = clone.biome;
        if (clone.encodedData != null) {
            this.encodedData = clone.encodedData.clone();
            this.palette = clone.palette.clone();
        }
    }

    public BiomePalette(final int[] biomeColors) {
        for (int i = 0; i < 256; i++) {
            this.set(i, biomeColors[i]);
        }
    }

    public BiomePalette() {
        this.biome = Integer.MIN_VALUE;
    }

    public int get(final int x, final int z) {
        return this.get(this.getIndex(x, z));
    }

    public synchronized int get(final int index) {
        if (this.encodedData == null) {
            return this.biome;
        }
        return this.palette.getKey(this.encodedData.getAt(index));
    }

    public void set(final int x, final int z, final int value) {
        this.set(this.getIndex(x, z), value);
    }

    public synchronized void set(final int index, final int value) {
        if (this.encodedData == null) {
            if (value == this.biome) {
                return;
            }
            if (this.biome == Integer.MIN_VALUE) {
                this.biome = value;
                return;
            }
            synchronized (this) {
                this.palette = new IntPalette();
                this.palette.add(this.biome);
                this.palette.add(value);
                this.encodedData = new BitArray256(1);
                if (value < this.biome) {
                    Arrays.fill(this.encodedData.data, -1);
                    this.encodedData.setAt(index, 0);
                } else {
                    this.encodedData.setAt(index, 1);
                }
                return;
            }
        }

        final int encodedValue = this.palette.getValue(value);
        if (encodedValue != Integer.MIN_VALUE) {
            this.encodedData.setAt(index, encodedValue);
        } else {
            synchronized (this) {
                final int[] raw = this.encodedData.toRaw(ThreadCache.intCache256.get());

                // TODO skip remapping of raw data and use grow instead if `remap`
                // boolean remap = value < palette.getValue(palette.length() - 1);

                for (int i = 0; i < 256; i++) {
                    raw[i] = this.palette.getKey(raw[i]);
                }

                final int oldRaw = raw[4];

                raw[index] = value;

                this.palette.add(value);

                final int oldBits = MathHelper.log2(this.palette.length() - 2);
                final int newBits = MathHelper.log2(this.palette.length() - 1);
                if (oldBits != newBits) {
                    this.encodedData = new BitArray256(newBits);
                }

                for (int i = 0; i < raw.length; i++) {
                    raw[i] = this.palette.getValue(raw[i]);
                }

                this.encodedData.fromRaw(raw);
            }
        }

    }

    public synchronized int[] toRaw() {
        int[] buffer = ThreadCache.intCache256.get();
        if (this.encodedData == null) {
            Arrays.fill(buffer, this.biome);
        } else {
            synchronized (this) {
                buffer = this.encodedData.toRaw(buffer);
                for (int i = 0; i < 256; i++) {
                    buffer[i] = this.palette.getKey(buffer[i]);
                }
            }
        }
        return buffer;
    }

    public int getIndex(final int x, final int z) {
        return z << 4 | x;
    }

    @Override
    public synchronized BiomePalette clone() {
        return new BiomePalette(this);
    }

}

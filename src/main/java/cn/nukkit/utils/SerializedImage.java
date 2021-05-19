package cn.nukkit.utils;

import io.netty.util.internal.EmptyArrays;
import lombok.ToString;

import java.util.Objects;

import static cn.nukkit.entity.data.Skin.*;

@ToString(exclude = {"data"})
public class SerializedImage {
    public static final SerializedImage EMPTY = new SerializedImage(0, 0, EmptyArrays.EMPTY_BYTES);

    public final int width;
    public final int height;
    public final byte[] data;

    public SerializedImage(int width, int height, byte[] data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public static SerializedImage fromLegacy(byte[] skinData) {
        Objects.requireNonNull(skinData, "skinData");
        switch (skinData.length) {
            case SINGLE_SKIN_SIZE:
                return new SerializedImage(64, 32, skinData);
            case DOUBLE_SKIN_SIZE:
                return new SerializedImage(64, 64, skinData);
            case SKIN_128_64_SIZE:
                return new SerializedImage(128, 64, skinData);
            case SKIN_128_128_SIZE:
                return new SerializedImage(128, 128, skinData);
        }
        throw new IllegalArgumentException("Unknown legacy skin size");
    }
}

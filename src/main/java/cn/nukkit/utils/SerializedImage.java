package cn.nukkit.utils;

import lombok.ToString;

@ToString(exclude = {"data"})
public class SerializedImage {
    public static final SerializedImage EMPTY = new SerializedImage(0, 0, new byte[0]);

    public final int width;
    public final int height;
    public final byte[] data;

    public SerializedImage(int width, int height, byte[] data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }
}

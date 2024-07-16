package cn.nukkit.utils;

import lombok.ToString;

/**
 * Skin animation
 */
@ToString
public class SkinAnimation {

    public final SerializedImage image;
    public final int type;
    public final float frames;
    public final int expression;

    public SkinAnimation(SerializedImage image, int type, float frames, int expression) {
            this.image = image;
            this.type = type;
            this.frames = frames;
            this.expression = expression;
    }
}

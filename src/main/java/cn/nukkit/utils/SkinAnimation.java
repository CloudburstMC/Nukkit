package cn.nukkit.utils;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.ToString;

@ToString
public class SkinAnimation {
    public final SerializedImage image;
    public final int type;
    public final float frames;
    @Since("1.3.2.0-PN") public final int expression;

    @PowerNukkitOnly("Re-added for backward-compatibility")
    @Deprecated @DeprecationDetails(since = "1.3.2.0-PN",
            reason = "The expression field was added and the constructor's signature was changed",
            replaceWith = "SkinAnimation(SerializedImage image, int type, float frames, int expression)")
    public SkinAnimation(SerializedImage image, int type, float frames) {
        this(image, type, frames, 0);
    }

    @Since("1.3.2.0-PN")
    public SkinAnimation(SerializedImage image, int type, float frames, int expression) {
        this.image = image;
        this.type = type;
        this.frames = frames;
        this.expression = expression;
    }
}

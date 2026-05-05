package cn.nukkit.network.protocol.types.debugshape;

import cn.nukkit.math.Vector3f;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Color;

@Data
@EqualsAndHashCode(callSuper = true)
public class DebugText extends DebugShape {

    String text;
    /**
     * @since v975
     */
    boolean useRotation;
    /**
     * @since v975
     */
    @Nullable
    Color backgroundColor;
    /**
     * @since v975
     */
    boolean depthTest;
    /**
     * @since v975
     */
    boolean showBackface;
    /**
     * @since v975
     */
    boolean showTextBackface;

    public DebugText() {
    }

    @Deprecated
    public DebugText(long id, int dimension, @Nullable Vector3f position, @Nullable Float scale, @Nullable Vector3f rotation, @Nullable Float totalTimeLeft, @Nullable Color color, String text, @Nullable Long attachedToEntityId) {
        setId(id);
        setDimension(dimension);
        setPosition(position);
        setScale(scale);
        setRotation(rotation);
        setTotalTimeLeft(totalTimeLeft);
        setColor(color);
        setAttachedToEntityId(attachedToEntityId);

        setText(text);
    }

    @Override
    public Type getType() {
        return Type.TEXT;
    }
}

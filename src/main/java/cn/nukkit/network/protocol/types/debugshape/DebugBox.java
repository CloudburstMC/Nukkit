package cn.nukkit.network.protocol.types.debugshape;

import cn.nukkit.math.Vector3f;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Color;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DebugBox extends DebugShape {

    Vector3f boxBounds;

    public DebugBox() {
    }

    @Deprecated
    public DebugBox(long id, int dimension, @Nullable Vector3f position, @Nullable Float scale, @Nullable Vector3f rotation, @Nullable Float totalTimeLeft, @Nullable Color color, Vector3f boxBounds, @Nullable Long attachedToEntityId) {
        setId(id);
        setDimension(dimension);
        setPosition(position);
        setScale(scale);
        setRotation(rotation);
        setTotalTimeLeft(totalTimeLeft);
        setColor(color);
        setAttachedToEntityId(attachedToEntityId);

        setBoxBounds(boxBounds);
    }

    @Override
    public Type getType() {
        return Type.BOX;
    }
}

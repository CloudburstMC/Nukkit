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
public class DebugCircle extends DebugShape {

    Integer segments;

    public DebugCircle() {
    }

    @Deprecated
    public DebugCircle(long id, int dimension, @Nullable Vector3f position, @Nullable Float scale, @Nullable Vector3f rotation, @Nullable Float totalTimeLeft, @Nullable Color color, Integer segments, @Nullable Long attachedToEntityId) {
        setId(id);
        setDimension(dimension);
        setPosition(position);
        setScale(scale);
        setRotation(rotation);
        setTotalTimeLeft(totalTimeLeft);
        setColor(color);
        setAttachedToEntityId(attachedToEntityId);

        setSegments(segments);
    }

    @Override
    public Type getType() {
        return Type.CIRCLE;
    }
}

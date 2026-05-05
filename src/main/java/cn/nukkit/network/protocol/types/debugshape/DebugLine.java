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
public class DebugLine extends DebugShape {

    Vector3f lineEndPosition;

    public DebugLine() {
    }

    @Deprecated
    public DebugLine(long id, int dimension, @Nullable Vector3f position, @Nullable Float scale, @Nullable Vector3f rotation, @Nullable Float totalTimeLeft, @Nullable Color color, Vector3f lineEndPosition, @Nullable Long attachedToEntityId) {
        setId(id);
        setDimension(dimension);
        setPosition(position);
        setScale(scale);
        setRotation(rotation);
        setTotalTimeLeft(totalTimeLeft);
        setColor(color);
        setAttachedToEntityId(attachedToEntityId);

        setLineEndPosition(lineEndPosition);
    }

    @Override
    public Type getType() {
        return Type.LINE;
    }
}

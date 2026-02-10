package cn.nukkit.network.protocol.types.debugshape;

import cn.nukkit.math.Vector3f;
import lombok.EqualsAndHashCode;
import lombok.Value;

import javax.annotation.Nullable;
import java.awt.*;

@Value
@EqualsAndHashCode(callSuper = true)
public class DebugLine extends DebugShape {

    Vector3f lineEndPosition;

    public DebugLine(long id, int dimension, @Nullable Vector3f position, @Nullable Float scale, @Nullable Vector3f rotation, @Nullable Float totalTimeLeft, @Nullable Color color, Vector3f lineEndPosition, @Nullable Long attachedToEntityId) {
        super(id, dimension, position, scale, rotation, totalTimeLeft, color, attachedToEntityId);
        this.lineEndPosition = lineEndPosition;
    }

    @Override
    public Type getType() {
        return Type.LINE;
    }
}

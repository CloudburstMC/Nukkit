package cn.nukkit.network.protocol.types.debugshape;

import cn.nukkit.math.Vector3f;
import lombok.EqualsAndHashCode;
import lombok.Value;

import javax.annotation.Nullable;
import java.awt.*;

@Value
@EqualsAndHashCode(callSuper = true)
public class DebugArrow extends DebugShape {

    Vector3f arrowEndPosition;
    Float arrowHeadLength;
    Float arrowHeadRadius;
    Integer arrowHeadSegments;

    public DebugArrow(long id, int dimension, @Nullable Vector3f position, @Nullable Float scale, @Nullable Vector3f rotation, @Nullable Float totalTimeLeft, @Nullable Color color, Vector3f arrowEndPosition, Float arrowHeadLength, Float arrowHeadRadius, Integer arrowHeadSegments, @Nullable Long attachedToEntityId) {
        super(id, dimension, position, scale, rotation, totalTimeLeft, color, attachedToEntityId);
        this.arrowEndPosition = arrowEndPosition;
        this.arrowHeadLength = arrowHeadLength;
        this.arrowHeadRadius = arrowHeadRadius;
        this.arrowHeadSegments = arrowHeadSegments;
    }

    @Override
    public Type getType() {
        return Type.ARROW;
    }
}

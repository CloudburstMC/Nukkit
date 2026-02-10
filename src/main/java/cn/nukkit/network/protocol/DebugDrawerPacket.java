package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.debugshape.*;
import cn.nukkit.utils.BinaryStream;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class DebugDrawerPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.__INTERNAL__SERVER_SCRIPT_DEBUG_DRAWER_PACKET;

    @Getter
    @Setter
    private List<DebugShape> shapes = new ArrayList<>();

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.shapes.size());
        for (DebugShape shape : this.shapes) {
            putUnsignedVarLong(shape.getId());

            if (shape.getType() != null) {
                putBoolean(true);
                putByte((byte) shape.getType().ordinal());
            } else putBoolean(false);

            putOptionalNull(shape.getPosition(), BinaryStream::putVector3f);
            putOptionalNull(shape.getScale(), BinaryStream::putLFloat);
            putOptionalNull(shape.getRotation(), BinaryStream::putVector3f);
            putOptionalNull(shape.getTotalTimeLeft(), BinaryStream::putLFloat);

            if (shape.getColor() != null) {
                putBoolean(true);
                putLInt(shape.getColor().getRGB());
            } else putBoolean(false);

            putOptionalNull(shape.getDimension(), BinaryStream::putVarInt);

            if (shape.getAttachedToEntityId() != null) {
                putBoolean(true);
                putUnsignedVarLong(shape.getAttachedToEntityId());
            } else putBoolean(false);

            putUnsignedVarInt(toPayloadType(shape.getType()));

            if (shape.getType() == null) {
                continue;
            }

            switch (shape.getType()) {
                case ARROW:
                    DebugArrow arrow = (DebugArrow) shape;
                    putOptionalNull(arrow.getArrowEndPosition(), BinaryStream::putVector3f);
                    putOptionalNull(arrow.getArrowHeadLength(), BinaryStream::putLFloat);
                    putOptionalNull(arrow.getArrowHeadRadius(), BinaryStream::putLFloat);

                    if (arrow.getArrowHeadSegments() != null) {
                        putBoolean(true);
                        putByte(arrow.getArrowHeadSegments().byteValue());
                    } else putBoolean(false);
                    break;
                case BOX:
                    DebugBox box = (DebugBox) shape;
                    putVector3f(box.getBoxBounds());
                    break;
                case CIRCLE:
                    DebugCircle circle = (DebugCircle) shape;
                    putByte(circle.getSegments().byteValue());
                    break;
                case LINE:
                    DebugLine line = (DebugLine) shape;
                    putVector3f(line.getLineEndPosition());
                    break;
                case SPHERE:
                    DebugSphere sphere = (DebugSphere) shape;
                    putByte(sphere.getSegments().byteValue());
                    break;
                case TEXT:
                    DebugText text = (DebugText) shape;
                    putString(text.getText());
                    break;
            }
        }
    }

    private static int toPayloadType(DebugShape.Type type) {
        if (type == null) {
            return 0;
        }

        switch (type) {
            case ARROW:
                return 1;
            case TEXT:
                return 2;
            case BOX:
                return 3;
            case LINE:
                return 4;
            case SPHERE:
            case CIRCLE:
                return 5;
            default:
                throw new IllegalStateException("Unknown debug shape type: " + type);
        }
    }
}

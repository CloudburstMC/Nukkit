package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector2f;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.types.*;
import lombok.Getter;
import lombok.ToString;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@ToString
@Getter
public class PlayerAuthInputPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_AUTH_INPUT_PACKET;

    private float yaw;
    private float pitch;
    private float headYaw;
    private Vector3f position;
    private Vector2 motion; // Vector2 for backwards compatibility
    private final Set<AuthInputAction> inputData = EnumSet.noneOf(AuthInputAction.class);
    private InputMode inputMode;
    private ClientPlayMode playMode;
    private AuthInteractionModel interactionModel;
    private long tick;
    private Vector3f delta;
    private final Map<PlayerActionType, PlayerBlockActionData> blockActionData = new EnumMap<>(PlayerActionType.class);
    private long predictedVehicle;
    private Vector2f analogMoveVector;
    private Vector2f vehicleRotation;
    private Vector2f interactRotation;
    private Vector3f cameraOrientation;
    private Vector2f rawMoveVector;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.pitch = this.getLFloat();
        this.yaw = this.getLFloat();
        this.position = this.getVector3f();
        this.motion = new Vector2(this.getLFloat(), this.getLFloat());
        this.headYaw = this.getLFloat();

        long inputData = this.getUnsignedVarLong();
        for (int i = 0; i < AuthInputAction.size(); i++) {
            if ((inputData & (1L << i)) != 0) {
                this.inputData.add(AuthInputAction.from(i));
            }
        }

        this.inputMode = InputMode.fromOrdinal((int) this.getUnsignedVarInt());
        this.playMode = ClientPlayMode.fromOrdinal((int) this.getUnsignedVarInt());

        this.interactionModel = AuthInteractionModel.fromOrdinal((int) this.getUnsignedVarInt());

        this.interactRotation = this.getVector2f();

        this.tick = this.getUnsignedVarLong();
        this.delta = this.getVector3f();

        if (this.inputData.contains(AuthInputAction.PERFORM_BLOCK_ACTIONS)) {
            int arraySize = this.getVarInt();
            if (arraySize > 256) throw new IllegalArgumentException("PlayerAuthInputPacket PERFORM_BLOCK_ACTIONS is too long: " + arraySize);
            for (int i = 0; i < arraySize; i++) {
                PlayerActionType type = PlayerActionType.from(this.getVarInt());
                switch (type) {
                    case START_DESTROY_BLOCK:
                    case ABORT_DESTROY_BLOCK:
                    case CRACK_BLOCK:
                    case PREDICT_DESTROY_BLOCK:
                    case CONTINUE_DESTROY_BLOCK:
                        this.blockActionData.put(type, new PlayerBlockActionData(type, this.getSignedBlockPosition(), this.getVarInt()));
                        break;
                    default:
                        this.blockActionData.put(type, new PlayerBlockActionData(type, null, -1));
                }
            }
        }

        if (this.inputData.contains(AuthInputAction.IN_CLIENT_PREDICTED_IN_VEHICLE)) {
            this.vehicleRotation = this.getVector2f();
            this.predictedVehicle = this.getVarLong();
        }

        this.analogMoveVector = this.getVector2f();
        this.cameraOrientation = this.getVector3f();
        this.rawMoveVector = this.getVector2f();
    }

    @Override
    public void encode() {
        this.encodeUnsupported();
    }
}

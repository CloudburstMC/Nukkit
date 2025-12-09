package cn.nukkit.network.protocol;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class AnimatePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ANIMATE_PACKET;

    public long eid;
    public Action action;
    public float data;
    @Deprecated
    public float rowingTime;

    @Override
    public void decode() {
        this.action = Action.fromId(this.getByte());
        if (this.action == null) {
            this.action = Action.NO_ACTION;
        }
        this.eid = getEntityRuntimeId();
        this.data = this.getLFloat();
        if (this.getBoolean()) {
            this.getString(); // Swing source
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.action.getId());
        this.putEntityRuntimeId(this.eid);
        this.putLFloat(this.data);
        this.putBoolean(false); // Swing source (optional)
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public enum Action {
        NO_ACTION(0),
        SWING_ARM(1),
        WAKE_UP(3),
        CRITICAL_HIT(4),
        MAGIC_CRITICAL_HIT(5),
        ROW_RIGHT(128),
        ROW_LEFT(129);

        private static final Int2ObjectMap<Action> ID_LOOKUP = new Int2ObjectOpenHashMap<>();

        static {
            for (Action value : values()) {
                ID_LOOKUP.put(value.id, value);
            }
        }

        private final int id;

        Action(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Action fromId(int id) {
            return ID_LOOKUP.get(id);
        }
    }
}

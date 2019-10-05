package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class AnimatePacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.ANIMATE_PACKET;


    public long eid;
    public Action action;
    public float rowingTime;

    @Override
    protected void decode(ByteBuf buffer) {
        this.action = Action.fromId(Binary.readVarInt(buffer));
        this.eid = Binary.readEntityRuntimeId(buffer);
        if (this.action == Action.ROW_RIGHT || this.action == Action.ROW_LEFT) {
            this.rowingTime = buffer.readFloatLE();
        }
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeVarInt(buffer, this.action.getId());
        Binary.writeEntityRuntimeId(buffer, this.eid);
        if (this.action == Action.ROW_RIGHT || this.action == Action.ROW_LEFT) {
            buffer.writeFloatLE(this.rowingTime);
        }
    }

    @Override
    public short pid() {
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

package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nukkit Project Team
 */
@ToString
public class AddPaintingPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ADD_PAINTING_PACKET;

    public long entityUniqueId;
    public long entityRuntimeId;
    public Vector3f position = new Vector3f();
    public int direction;
    public Painting painting;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.entityUniqueId = this.getEntityUniqueId();
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.position = this.getVector3f();
        this.direction = this.getVarInt();
        this.painting = Painting.getByName(this.getString());
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.entityUniqueId);
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putVector3f(this.position);
        this.putVarInt(this.direction);
        this.putString(this.painting.name());
    }

    public enum Painting {

        KEBAB("Kebab", 1, 1),
        AZTEC("Aztec", 1, 1),
        ALBAN("Alban", 1, 1),
        AZTEC2("Aztec2", 1, 1),
        BOMB("Bomb", 1, 1),
        PLANT("Plant", 1, 1),
        WASTELAND("Wasteland", 1, 1),
        WANDERER("Wanderer", 1, 2),
        GRAHAM("Graham", 1, 2),
        POOL("Pool", 2, 1),
        COURBET("Courbet", 2, 1),
        SUNSET("Sunset", 2, 1),
        SEA("Sea", 2, 1),
        CREEBET("Creebet", 2, 1),
        MATCH("Match", 2, 2),
        BUST("Bust", 2, 2),
        STAGE("Stage", 2, 2),
        VOID("Void", 2, 2),
        SKULL_AND_ROSES("SkullAndRoses", 2, 2),
        WITHER("Wither", 2, 2),
        FIGHTERS("Fighters", 4, 2),
        SKELETON("Skeleton", 4, 3),
        DONKEY_KONG("DonkeyKong", 4, 3),
        POINTER("Pointer", 4, 4),
        PIG_SCENE("Pigscene", 4, 4),
        FLAMING_SKULL("Flaming Skull", 4, 4);

        public final String name;
        public final int width;
        public final int height;

        private static final Map<String, Painting> PAINTINGS = new HashMap<>();

        static {
            for (Painting painting : values()) {
                PAINTINGS.put(painting.name, painting);
            }
        }

        Painting(String name, int width, int height) {
            this.name = name;
            this.width = width;
            this.height = height;
        }

        public static Painting getByName(String name) {
            return PAINTINGS.get(name);
        }
    }
}

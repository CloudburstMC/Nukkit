package cn.nukkit.server.network.minecraft.data;

import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.server.nbt.util.VarInt;
import cn.nukkit.server.network.minecraft.MinecraftUtil;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

public enum MetadataConstants {
    FLAGS(Type.LONG),
    HEALTH(Type.INT),
    VARIANT(Type.INT),
    COLOR(Type.BYTE),
    NAMETAG(Type.STRING),
    OWNER_EID(Type.LONG),
    TARGET_EID(Type.LONG),
    AIR(Type.SHORT),
    POTION_COLOR(Type.INT),
    POTION_AMBIENT(Type.BYTE),
    UNKNOWN_10(Type.BYTE),
    HURT_TIME(Type.INT),
    HURT_DIRECTION(Type.INT),
    PADDLE_TIME_LEFT(Type.FLOAT),
    PADDLE_TIME_RIGHT(Type.FLOAT),
    EXPERIENCE_VALUE(Type.INT),
    DISPLAY_ITEM(Type.INT),
    DISPLAY_OFFSET(Type.INT),
    HAS_DISPLAY(Type.BYTE),
    UNKNOWN_19(null),
    UNKNOWN_20(null),
    UNKNOWN_21(null),
    UNKNOWN_22(null),
    ENDERMAN_HELD_ITEM_ID(Type.SHORT),
    ENTITY_AGE(Type.SHORT),
    UNKNOWN_25(null),
    UNKNOWN_26(null),
    UNKNOWN_27(null),
    UNKNOWN_28(null),
    FIREBALL_POWER_X(Type.FLOAT),
    FIREBALL_POWER_Y(Type.FLOAT),
    FIREBALL_POWER_Z(Type.FLOAT),
    UNKNOWN_32(null),
    UNKNOWN_33(null),
    UNKNOWN_34(null),
    UNKNOWN_35(null),
    POTION_AUX_VALUE(Type.SHORT),
    LEAD_HOLDER_EID(Type.LONG),
    SCALE(Type.FLOAT),
    INTERACTIVE_TAG(Type.STRING),
    NPC_SKIN_ID(Type.STRING),
    URL_TAG(Type.STRING),
    MAX_AIR(Type.SHORT),
    MAX_VARIANT(Type.INT),
    CONTAINER_TYPE(Type.BYTE),
    CONTAINER_BASE_SIZE(Type.INT),
    CONTAINER_EXTRA_SLOTS_PER_STRENGTH(Type.INT),
    BLOCK_TARGET(Type.VECTOR3I),
    WITHER_INVULNERABLE_TICKS(Type.INT),
    WITHER_TARGET_1(Type.LONG),
    WITHER_TARGET_2(Type.LONG),
    WITHER_TARGET_3(Type.LONG),
    WITHER_AERIAL_ATTACK(Type.SHORT),
    BOUNDING_BOX_WIDTH(Type.FLOAT),
    BOUNDING_BOX_HEIGHT(Type.FLOAT),
    FUSE_LENGTH(Type.INT),
    RIDER_SEAT_POSITION(Type.VECTOR3F),
    RIDER_ROTATION_LOCKED(Type.BYTE),
    RIDER_MAX_ROTATION(Type.FLOAT),
    RIDER_MIN_ROTATION(Type.FLOAT),
    AREA_EFFECT_CLOUD_RADIUS(Type.FLOAT),
    AREA_EFFECT_CLOUD_WATING(Type.INT),
    AREA_EFFECT_CLOUD_PARTICLE_ID(Type.INT),
    SHULKER_PEAK_HEIGHT(Type.INT),
    SHULKER_ATTACH_FACE(Type.BYTE),
    UNKNOWN_65(Type.SHORT),
    SHULKER_ATTACK_POS(Type.VECTOR3I),
    TRADING_PLAYER_EID(Type.LONG),
    UNKNOWN_68(null),
    UNKNOWN_69(Type.BYTE),
    COMMAND_BLOCK_COMMAND(Type.STRING),
    COMMAND_BLOCK_LAST_OUTPUT(Type.STRING),
    COMMAND_BLOCK_TRACK_OUTPUT(Type.BYTE),
    CONTROLLING_RIDER_SEAT_NUMBER(Type.BYTE),
    STRENGTH(Type.INT),
    MAX_STRENGTH(Type.INT),
    UNKNOWN_76(Type.INT),
    LIMITED_LIFE(Type.INT),
    ARMOR_STAND_POSE_INDEX(Type.INT),
    ENDER_CRYSTAL_TIME_OFFSET(Type.INT),
    UNKNOWN_80(null),
    COLOR_2(Type.BYTE),
    UNKNOWN_82(null),
    SCORE_TAG(Type.STRING),
    BALLOON_ATTACHED_ENTITY(Type.LONG),
    PUFFERFISH_SIZE(Type.BYTE);

    @Getter
    private final Type type;

    MetadataConstants(Type type) {
        this.type = type;
    }

    @Log4j2
    public enum Flag {
        ON_FIRE,
        SNEAKING,
        RIDING,
        SPRINTING,
        ACTION,
        INVISIBLE,
        TEMPTED,
        IN_LOVE,
        SADDLED,
        POWERED,
        IGNITED,
        BABY,
        CONVERTING,
        CRITICAL,
        CAN_SHOW_NAMETAG,
        ALWAYS_SHOW_NAMETAG,
        NO_AI,
        SILENT,
        WALLCLIMBING,
        CAN_CLIMB,
        SWIMMER,
        CAN_FLY,
        WALKER,
        RESTING,
        SITTING,
        ANGRY,
        INTERESTED,
        CHARGED,
        TAMED,
        LEASHED,
        SHEARED,
        GLIDING,
        ELDER,
        MOVING,
        BREATHING,
        CHESTED,
        STACKABLE,
        SHOW_BASE,
        REARING,
        VIBRATING,
        IDLING,
        EVOKER_SPELL,
        CHARGE_ATTACK,
        WASD_CONTROLLED,
        CAN_POWER_JUMP,
        LINGER,
        HAS_COLLISION,
        AFFECTED_BY_GRAVITY,
        FIRE_IMMUNE,
        DANCING,
        ENCHANTED,
        UNKNOWN_TRIDENT,
        CONTAINER_PRIVATE,
        TRANSFORM,
        SPIN_ATTACK,
        SWIMMING,
        BRIBED
    }

    public enum Type {
        BYTE {
            @Override
            public Object read(ByteBuf buf) {
                return buf.readByte();
            }

            @Override
            public void write(ByteBuf buf, Object o) {
                buf.writeByte((byte) o);
            }
        },
        SHORT {
            @Override
            public Object read(ByteBuf buf) {
                return buf.readShortLE();
            }

            @Override
            public void write(ByteBuf buf, Object o) {
                buf.writeShortLE((short) o);
            }
        },
        INT {
            @Override
            public Object read(ByteBuf buf) {
                return VarInt.readSignedInt(buf);
            }

            @Override
            public void write(ByteBuf buf, Object o) {
                VarInt.writeSignedInt(buf, (int) o);
            }
        },
        FLOAT {
            @Override
            public Object read(ByteBuf buf) {
                return buf.readFloatLE();
            }

            @Override
            public void write(ByteBuf buf, Object o) {
                buf.writeFloatLE((float) o);
            }
        },
        STRING {
            @Override
            public Object read(ByteBuf buf) {
                return MinecraftUtil.readString(buf);
            }

            @Override
            public void write(ByteBuf buf, Object o) {
                MinecraftUtil.writeString(buf, (String) o);
            }
        },
        ITEM {
            @Override
            public Object read(ByteBuf buf) {
                return MinecraftUtil.readItemInstance(buf);
            }

            @Override
            public void write(ByteBuf buf, Object o) {
                MinecraftUtil.writeItemInstance(buf, (ItemInstance) o);
            }
        },
        VECTOR3I {
            @Override
            public Object read(ByteBuf buf) {
                return MinecraftUtil.readVector3i(buf);
            }

            @Override
            public void write(ByteBuf buf, Object o) {
                MinecraftUtil.writeVector3i(buf, (Vector3i) o);
            }
        },
        LONG {
            @Override
            public Object read(ByteBuf buf) {
                return VarInt.readSignedLong(buf);
            }

            @Override
            public void write(ByteBuf buf, Object o) {
                VarInt.writeSignedLong(buf, (long) o);
            }
        },
        VECTOR3F {
            @Override
            public Object read(ByteBuf buf) {
                return MinecraftUtil.readVector3f(buf);
            }

            @Override
            public void write(ByteBuf buf, Object o) {
                MinecraftUtil.writeVector3f(buf, (Vector3f) o);
            }
        };

        public abstract Object read(ByteBuf buf);

        public abstract void write(ByteBuf buf, Object o);
    }
}

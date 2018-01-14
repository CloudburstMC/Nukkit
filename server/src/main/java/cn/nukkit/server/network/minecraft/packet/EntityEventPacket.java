package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.readSignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeSignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.readRuntimeEntityId;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeRuntimeEntityId;

@Data
public class EntityEventPacket implements MinecraftPacket {
    private long runtimeEntityId;
    private Event event;
    private int data;

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, runtimeEntityId);
        buffer.writeByte(event.ordinal());
        writeSignedInt(buffer, data);
    }

    @Override
    public void decode(ByteBuf buffer) {
        runtimeEntityId = readRuntimeEntityId(buffer);
        event = Event.values()[buffer.readByte()];
        data = readSignedInt(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }

    public enum Event {
        UNKNOWN_0,
        UNKNOWN_1,
        HURT_ANIMATION,
        DEATH_ANIMATION,
        ARM_SWING,
        UNKNOWN_5,
        TAME_FAIL,
        TAME_SUCCESS,
        SHAKE_WET,
        USE_ITEM,
        EAT_GRASS_ANIMATION,
        FISH_HOOK_BUBBLE,
        FISH_HOOK_POSITION,
        FISH_HOOK_HOOK,
        FISH_HOOK_TEASE,
        SQUID_INK_CLOUD,
        ZOMBIE_VILLAGER_CURED,
        UNKNOWN_17,
        RESPAWN,
        IRON_GOLEM_OFFER_FLOWER,
        IRON_GOLEM_WITHDRAW_FLOWER,
        LOVE_PARTICLES,
        UNKNOWN_22,
        UNKNOWN_23,
        WITCH_SPELL_PARTICLES,
        FIREWORK_PARTICLES,
        UNKNOWN_26,
        SILVERFISH_SPAWN_ANIMATION,
        UNKNOWN_28,
        WITCH_DRINK_POTION,
        WITCH_THROW_POTION,
        MINECART_TNT_PRIME_FUSE,
        UNKNOWN_32,
        UNKNOWN_33,
        PLAYER_ADD_XP_LEVELS,
        ELDER_GUARDIAN_CURSE,
        AGENT_ARM_SWING,
        ENDER_DRAGON_DEATH,
        DUST_PARTICLES,
        UNKNOWN_39,
        UNKNOWN_40,
        UNKNOWN_41,
        UNKNOWN_42,
        UNKNOWN_43,
        UNKNOWN_44,
        UNKNOWN_45,
        UNKNOWN_46,
        UNKNOWN_47,
        UNKNOWN_48,
        UNKNOWN_49,
        UNKNOWN_50,
        UNKNOWN_51,
        UNKNOWN_52,
        UNKNOWN_53,
        UNKNOWN_54,
        UNKNOWN_55,
        UNKNOWN_56,
        EATING_ITEM,
        UNKNOWN_58,
        UNKNOWN_59,
        BABY_ANIMAL_FEED,
        DEATH_SMOKE_CLOUD,
        COMPLETE_TRADE,
        REMOVE_LEASH,
        UNKNOWN_64,
        CONSUME_TOTEM,
        PLAYER_CHECK_TREASURE_HUNTER_ACHIEVEMENT,
        ENTITY_SPAWN,
        DRAGON_VOMIT,
        ITEM_ENTITY_MERGE,

        //TODO: Would be lovely if we could fine out the unknowns.
    }
}

package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class EntityEventPacket extends DataPacket {

    public static final byte EVENT_HURT_ANIMATION = 2;
    public static final byte EVENT_DEATH_ANIMATION = 3;
    public static final byte EVENT_ARM_SWING = 4;
    public static final byte EVENT_STOP_ATTACK = 5;
    public static final byte EVENT_TAME_FAIL = 6;
    public static final byte EVENT_TAME_SUCCESS = 7;
    public static final byte EVENT_SHAKE_WET = 8;
    public static final byte EVENT_USE_ITEM = 9;
    public static final byte EVENT_EAT_GRASS_ANIMATION = 10;
    public static final byte EVENT_FISH_HOOK_BUBBLE = 11;
    public static final byte EVENT_FISH_HOOK_POSITION = 12;
    public static final byte EVENT_FISH_HOOK_HOOK = 13;
    public static final byte EVENT_FISH_HOOK_TEASE = 14;
    public static final byte EVENT_SQUID_INK_CLOUD = 15;
    public static final byte EVENT_ZOMBIE_VILLAGER_CURE = 16;
    public static final byte EVENT_AMBIENT_SOUND = 17;
    public static final byte EVENT_RESPAWN = 18;
    public static final byte EVENT_IRON_GOLEM_OFFER_FLOWER = 19;
    public static final byte EVENT_IRON_GOLEM_WITHDRAW_FLOWER = 20;
    public static final byte EVENT_LOVE_PARTICLES = 21;
    public static final byte EVENT_VILLAGER_ANGRY = 22;
    public static final byte EVENT_VILLAGER_HAPPY = 23;
    public static final byte EVENT_WITCH_SPELL_PARTICLES = 24;
    public static final byte EVENT_FIREWORK_EXPLOSION = 25;
    public static final byte EVENT_IN_LOVE_PARTICLES = 26;
    public static final byte EVENT_SILVERFISH_SPAWN_ANIMATION = 27;
    public static final byte EVENT_GUARDIAN_ATTACK = 28;
    public static final byte EVENT_WITCH_DRINK_POTION = 29;
    public static final byte EVENT_WITCH_THROW_POTION = 30;
    public static final byte EVENT_MINECART_TNT_PRIME_FUSE = 31;
    public static final byte EVENT_CREEPER_PRIME_FUSE = 32;
    public static final byte EVENT_AIR_SUPPLY_EXPIRED = 33;
    public static final byte EVENT_ENCHANT = 34;
    public static final byte EVENT_ELDER_GUARDIAN_CURSE = 35;
    public static final byte EVENT_AGENT_ARM_SWING = 36;
    public static final byte EVENT_ENDER_DRAGON_DEATH = 37;
    public static final byte EVENT_DUST_PARTICLES = 38;
    public static final byte EVENT_ARROW_SHAKE = 39;
    //TODO: 40-56
    public static final byte EVENT_EATING_ITEM = 57;
    //TODO: 58 and 59
    public static final byte EVENT_BABY_ANIMAL_FEED = 60;
    public static final byte EVENT_DEATH_SMOKE_CLOUD = 61;
    public static final byte EVENT_COMPLETE_TRADE = 62;
    public static final byte EVENT_REMOVE_LEASH = 63;
    //TODO: 64
    public static final byte EVENT_CONSUME_TOTEM = 65;
    public static final byte EVENT_PLAYER_CHECK_TREASURE_HUNTER_ACHIEVEMENT = 66;
    public static final byte EVENT_ENTITY_SPAWN = 67;
    public static final byte EVENT_DRAGON_PUKE = 68;
    public static final byte EVENT_MERGE_ITEMS = 69;
    public static final byte EVENT_START_SWIM = 70;
    public static final byte EVENT_BALLOON_POP = 71;
    public static final byte EVENT_TREASURE_HUNT = 72;
    public static final byte EVENT_AGENT_SUMMON = 73;
    public static final byte EVENT_CHARGED_CROSSBOW = 74;
    public static final byte EVENT_FALL = 75;

    public long entityRuntimeId;
    public byte event;
    public int data;

    @Override
    public byte pid() {
        return ProtocolInfo.ENTITY_EVENT_PACKET;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId();
        this.event = this.getByte();
        this.data = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putByte(this.event);
        this.putVarInt(this.data);
    }
}

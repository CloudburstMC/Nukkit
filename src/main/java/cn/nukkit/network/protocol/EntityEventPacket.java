package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import lombok.ToString;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
public class EntityEventPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.ENTITY_EVENT_PACKET;
    
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int NONE = 0;
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int JUMP = 1;
    public static final int HURT_ANIMATION = 2;
    public static final int DEATH_ANIMATION = 3;
    public static final int ARM_SWING = 4;
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int ATTACK_STOP = 5;
    public static final int TAME_FAIL = 6;
    public static final int TAME_SUCCESS = 7;
    public static final int SHAKE_WET = 8;
    public static final int USE_ITEM = 9;
    public static final int EAT_GRASS_ANIMATION = 10;
    public static final int FISH_HOOK_BUBBLE = 11;
    public static final int FISH_HOOK_POSITION = 12;
    public static final int FISH_HOOK_HOOK = 13;
    public static final int FISH_HOOK_TEASE = 14;
    public static final int SQUID_INK_CLOUD = 15;
    public static final int ZOMBIE_VILLAGER_CURE = 16;
    public static final int AMBIENT_SOUND = 17;
    public static final int RESPAWN = 18;
    public static final int IRON_GOLEM_OFFER_FLOWER = 19;
    public static final int IRON_GOLEM_WITHDRAW_FLOWER = 20;
    public static final int LOVE_PARTICLES = 21;
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int VILLAGER_ANGRY = 22;
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int VILLAGER_HAPPY = 23;
    public static final int WITCH_SPELL_PARTICLES = 24;
    public static final int FIREWORK_EXPLOSION = 25;
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int IN_LOVE_HEARTS = 26;
    public static final int SILVERFISH_SPAWN_ANIMATION = 27;
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int GUARDIAN_ATTACK_ANIMATION = 28;
    public static final int WITCH_DRINK_POTION = 29;
    public static final int WITCH_THROW_POTION = 30;
    public static final int MINECART_TNT_PRIME_FUSE = 31;
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int PRIME_CREEPER = 32;
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int AIR_SUPPLY = 33;
    public static final int ENCHANT = 34;
    public static final int ELDER_GUARDIAN_CURSE = 35;
    public static final int AGENT_ARM_SWING = 36;
    public static final int ENDER_DRAGON_DEATH = 37;
    public static final int DUST_PARTICLES = 38;
    public static final int ARROW_SHAKE = 39;

    public static final int EATING_ITEM = 57;

    public static final int BABY_ANIMAL_FEED = 60;
    public static final int DEATH_SMOKE_CLOUD = 61;
    public static final int COMPLETE_TRADE = 62;
    public static final int REMOVE_LEASH = 63;
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int CARAVAN = 64;
    public static final int CONSUME_TOTEM = 65;
    public static final int PLAYER_CHECK_TREASURE_HUNTER_ACHIEVEMENT = 66;
    public static final int ENTITY_SPAWN = 67;
    public static final int DRAGON_PUKE = 68;
    public static final int MERGE_ITEMS = 69;
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int START_SWIMMING = 70;
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int BALLOON_POP = 71;
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int TREASURE_HUNT = 72;
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int SUMMON_AGENT = 73;
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int FINISHED_CHARGING_CROSSBOW = 74;
    @PowerNukkitOnly @Since("1.4.0.0-PN") public static final int LANDED_ON_GROUND = 75;
    
    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public long eid;
    public int event;
    public int data;

    @Override
    public void decode() {
        this.eid = this.getEntityRuntimeId();
        this.event = this.getByte();
        this.data = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.eid);
        this.putByte((byte) this.event);
        this.putVarInt(this.data);
    }
}

package cn.nukkit.server.entity;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;

import javax.annotation.Nonnull;

public enum EntityEvent {
    HURT_ANIMATION(2),
    DEATH_ANIMATION(3),
    ARM_SWING(4),
    TAME_FAIL(6),
    TAME_SUCCESS(7),
    SHAKE_WET(8),
    USE_ITEM(9),
    EAT_GRASS_ANIMATION(10),
    FISH_HOOK_BUBBLE(11),
    FISH_HOOK_POSITION(12),
    FISH_HOOK_HOOK(13),
    FISH_HOOK_TEASE(14),
    SQUID_INK_CLOUD(15),
    ZOMBIE_VILLAGER_CURE(16),
    RESPAWN(18),
    IRON_GOLEM_OFFER_FLOWER(19),
    IRON_GOLEM_WITHDRAW_FLOWER(20),
    LOVE_PARTICLES(21), // Breeding
    UNKNOWN_0(22), // Tradeables
    UNKNOWN_1(23), // Tradeables
    WITCH_SPELL_PARTICLES(24),
    FIREWORK_PARTICLES(25),
    SILVERFISH_SPAWN_ANIMATION(27),
    WITCH_DRINK_POTION(29),
    WITCH_THROW_POTION(30),
    MINECART_TNT_PRIME_FUSE(31),
    PLAYER_ADD_XP_LEVELS(34),
    ELDER_GUARDIAN_CURSE(35),
    AGENT_ARM_SWING(36),
    ENDER_DRAGON_DEATH(37),
    DUST_PARTICLES(38), // Not sure what this is
    UNKNOWN_2(39),
    EATING_ITEM(57),
    BABY_ANIMAL_FEED(60), // Green particles, like bonemeal on crops
    DEATH_SMOKE_CLOUD(61),
    COMPLETE_TRADE(62),
    REMOVE_LEASH(63), // data 1 = cut leash
    LEAVE_CARAVAN(64), // That's what it's called...
    CONSUME_TOTEM(65),
    PLAYER_CHECK_TREASURE_HUNTER_ACHIEVEMENT(66), //mojang...
    ENTITY_SPAWN(67), // used for MinecraftEventing stuff, not needed
    PUKE(68), // they call this puke particles
    ITEM_ENTITY_MERGE(69); // data = new amount

    @Getter
    private final int id;
    private static final TIntObjectMap<EntityEvent> BY_ID = new TIntObjectHashMap<>();

    static {
        for (EntityEvent event : values()) {
            BY_ID.put(event.id, event);
        }
    }

    EntityEvent(int id) {
        this.id = id;
    }

    @Nonnull
    public static EntityEvent byId(int id) {
        if (!BY_ID.containsKey(id)) {
            throw new IllegalArgumentException("EntityEvent with id " + id + " does not exist");
        }
        return BY_ID.get(id);
    }
}

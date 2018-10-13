package com.nukkitx.server.entity;

import com.nukkitx.api.util.Identifier;

public enum EntityType {
    CHICKEN("minecraft:chicken", 10, 0.7f, 0.4f),
    COW("minecraft:cow", 11, 1.4f, 0.9f),
    PIG("minecraft:pig", 12, 0.9f),
    SHEEP("minecraft:sheep", 13, 1.3f, 0.9f),
    WOLF("minecraft:wolf", 14, 0.85f, 0.6f),
    VILLAGER("minecraft:villager", 15, 1.8f, 0.6f, 0.6f, 1.62f),
    MOOSHROOM("minecraft:mooshroom", 16, 1.4f, 0.9f),
    SQUID("minecraft:squid", 17, 0.8f),
    RABBIT("minecraft:rabbit", 18, 0.5f, 0.4f),
    BAT("minecraft:bat", 19, 0.9f, 0.5f),
    IRON_GOLEM("minecraft:iron_golem", 20, 2.7f, 1.4f),
    SNOW_GOLEM("minecraft:snow_golem", 21, 1.9f, 0.7f),
    OCELOT("minecraft:ocelot", 22, 0.35f, 0.3f),
    HORSE("minecraft:horse", 23, 1.6f, 1.3965f),
    DONKEY("minecraft:donkey", 24, 1.6f, 1.3965f),
    MULE("minecraft:mule", 25, 1.6f, 1.3965f),
    SKELETON_HORSE("minecraft:skeleton_horse", 26, 1.6f, 1.3965f),
    ZOMBIE_HORSE("minecraft:zombie_horse", 27, 1.6f, 1.3965f),
    POLAR_BEAR("minecraft:polar_bear", 28, 1.4f, 1.3f),
    LLAMA("minecraft:llama", 29, 1.87f, 0.9f),
    PARROT("minecraft:parrot", 30, 0.9f, 0.5f),
    DOLPHIN("minecraft:dolphin", 31, 0f),
    ZOMBIE("minecraft:zombie", 32, 1.8f, 0.6f, 0.6f, 1.62f),
    CREEPER("minecraft:creeper", 33, 1.7f, 0.6f, 0.6f, 1.62f),
    SKELETON("minecraft:skeleton", 34, 1.8f, 0.6f, 0.6f, 1.62f),
    SPIDER("minecraft:spider", 35, 0.9f, 1.4f, 1.4f, 1f),
    ZOMBIE_PIGMAN("minecraft:zombie_pigman", 36, 1.8f, 0.6f, 0.6f, 1.62f),
    SLIME("minecraft:slime", 37, 0.51f),
    ENDERMAN("minecraft:enderman", 38, 2.9f, 0.6f),
    SILVERFISH("minecraft:silverfish", 39, 0.3f, 0.4f),
    CAVE_SPIDER("minecraft:cave_spider", 40, 0.5f, 0.7f),
    GHAST("minecraft:ghast", 41, 4.0f),
    MAGMA_CUBE("minecraft:magma_cube", 42, 0.51f),
    BLAZE("minecraft:blaze", 43, 1.8f, 0.6f),
    ZOMBIE_VILLAGER("minecraft:zombie_villager", 44, 1.8f, 0.6f, 0.6f, 1.62f),
    WITCH("minecraft:witch", 45, 1.8f, 0.6f, 0.6f, 1.62f),
    STRAY("minecraft:stray", 46, 1.8f, 0.6f, 0.6f, 1.62f),
    HUSK("minecraft:husk", 47, 1.8f, 0.6f, 0.6f, 1.62f),
    WITHER_SKELETON("minecraft:wither_skeleton", 48, 2.4f, 0.7f),
    GUARDIAN("minecraft:guardian", 49, 0.85f),
    ELDER_GUARDIAN("minecraft:eleder_guardian", 50, 1.9975f),
    NPC("minecraft:npc", 51, 1.8f, 0.6f, 0.6f, 1.62f),
    WITHER("minecraft:wither", 52, 3.5f, 0.9f),
    ENDER_DRAGON("minecraft:ender_dragon", 53, 4f, 13f),
    SHULKER("minecraft:shulker", 54, 1f, 1f),
    ENDERMITE("minecraft:endermite", 55, 0.3f, 0.4f),
    AGENT("minecraft:agent", 56, 0f),
    VINDICATOR("minecraft:vindicator", 57, 1.8f, 0.6f, 0.6f, 1.62f),


    ARMOR_STAND("minecraft:armor_stand", 61, 0f),
    TRIPOD_CAMERA("minecraft:tripod_camera", 62, 0f),
    PLAYER("minecraft:player", 63, 1.8f, 0.6f, 0.6f, 1.62f),
    ITEM("minecraft:item", 64, 0.25f, 0.25f),
    PRIMED_TNT("minecraft:primed_tnt", 65, 0.98f, 0.98f),
    FALLING_BLOCK("minecraft:falling_block", 66, 0.98f, 0.98f),
    MOVING_BLOCK("minecraft:moving_block", 67, 0f),
    XP_BOTTLE("minecraft:xp_bottle", 68, 0.25f, 0.25f),
    XP_ORB("minecraft:xp_orb", 69, 0f),
    EYE_OF_ENDER_SIGNAL("minecraft:eye_of_ender_signal", 70, 0f),
    ENDER_CRYSTAL("minecraft:ender_crystal", 71, 0f),
    FIREWORKS_ROCKET("minecraft:fireworks_rocket", 72, 0f),
    TRIDENT("minecraft:thrown_trident", 73, 0f),

    SHULKER_BULLET("minecraft:shulker_bullet", 76, 0f),
    FISHING_HOOK("minecraft:fishing_hook", 77, 0f),
    CHALKBOARD("minecraft:chalkboard", 78, 0f),
    DRAGON_FIREBALL("minecraft:dragon_fireball", 79, 0f),
    ARROW("minecraft:arrow", 80, 0.25f, 0.25f),
    SNOWBALL("minecraft:snowball", 81, 0f),
    EGG("minecraft:egg", 82, 0f),
    PAINTING("minecraft:painting", 83, 0f),
    MINECART("minecraft:minecart", 84, 0f),
    FIREBALL("minecraft:fireball", 85, 0f),
    SPLASH_POTION("minecraft:splash_potion", 86, 0f),
    ENDER_PEARL("minecraft:ender_pearl", 87, 0f),
    LEASH_KNOT("minecraft:leash_knot", 88, 0f),
    WITHER_SKULL("minecraft:wither_skull", 89, 0f),
    BOAT("minecraft:boat", 90, 0.7f, 1.6f, 1.6f, 0.35f),
    WITHER_SKULL_DANGEROUS("minecraft:wither_skeleton_dangerous", 91, 0f),
    LIGHTNING_BOLT("minecraft:lightning_bolt", 93, 0f),
    SMALL_FIREBALL("minecraft:small_fireball", 94, 0f),
    AREA_EFFECT_CLOUD("minecraft:area_effect_cloud", 95, 0f),
    HOPPER_MINECART("minecraft:hopper_minecart", 96, 0f),
    TNT_MINECART("minecraft:tnt_minecard", 97, 0f),
    CHEST_MINECART("minecraft:chest_minecart", 98, 0f),

    COMMAND_BLOCK_MINECART("minecraft:command_block_minecart", 100, 0f),
    LINGERING_POTION("minecraft:lingering_potion", 101, 0f),
    LLAMA_SPIT("minecraft:llama_spit", 102, 0f),
    EVOCATION_FANG("minecraft:evocation_fang", 103, 0f),
    EVOCATION_ILLAGER("minecraft:evocation_illager", 104, 0f),
    VEX("minecraft:vex", 105, 0f),
    ICE_BOMB("minecraft:ice_bomb", 106, 0f),
    BALLOON("minecraft:balloon", 107, 0f), //TODO
    PUFFERFISH("minecraft:pufferfish", 108, 0.7f, 0.7f),
    SALMON("minecraft:salmon", 109, 0.5f, 0.7f),
    TROPICALFISH("minecraft:tropicalfish", 111, 0.6f, 0.6f),
    COD("minecraft:cod", 112, 0.25f, 0.5f);

    private final Identifier identifier;
    private final int type;
    private final float height;
    private final float width;
    private final float length;
    private final float offset;

    EntityType(String identifier, int type, float height) {
        this(identifier, type, height, 0f);
    }

    EntityType(String identifier, int type, float height, float width) {
        this(identifier, type, height, width, width);
    }

    EntityType(String identifier, int type, float height, float width, float length) {
        this(identifier, type, height, width, length, 0f);
    }

    EntityType(String identifier, int type, float height, float width, float length, float offset) {
        this.identifier = Identifier.get(identifier);
        this.type = type;
        this.height = height;
        this.width = width;
        this.length = length;
        this.offset = offset + 0.00001f;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public int getType() {
        return type;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public float getLength() {
        return length;
    }

    public float getOffset() {
        return offset;
    }
}
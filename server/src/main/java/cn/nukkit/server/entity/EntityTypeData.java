package cn.nukkit.server.entity;

public enum EntityTypeData {
    CHICKEN(10, 0.7f),
    COW(11, 1.3f),
    PIG(12, 0.9f),
    SHEEP(13, 1.3f),
    WOLF(14, 0.8f),
    VILLAGER(15, 1.8f, 0.6f, 0.6f, 1.62f),
    MUSHROOM_COW(16, 1.3f),
    SQUID(17, 0.95f, 0.95f, 0.95f, 1f),
    RABBIT(18, 0f),
    BAT(19, 0.3f),
    IRON_GOLEM(20, 2.9f),
    SNOW_GOLEM(21, 1.9f),
    OCELOT(22, 0.7f),
    HORSE(23, 1.3f),
    DONKEY(24, 0f),
    MULE(25, 0f),
    SKELETON_HORSE(26, 0f),
    ZOMBIE_HORSE(27, 0f),
    POLAR_BEAR(28, 0f),
    LLAMA(29, 0f),
    PARROT(30, 0f),

    ZOMBIE(32, 1.8f),
    CREEPER(33, 1.8f),
    SKELETON(34, 1.8f),
    SPIDER(35, 1.12f),
    ZOMBIE_PIGMAN(36, 1.8f),
    SLIME(37, 0),
    ENDERMAN(38, 2.9f),
    SILVERFISH(39, 0.3f),
    CAVE_SPIDER(40, 0.5f),
    GHAST(41, 4.0f),
    LAVA_SLIME(42, 0f),
    BLAZE(43, 1.8f),
    ZOMBIE_VILLAGER(44, 1.8f),
    WITCH(45, 1.8f),
    STRAY(46, 1.8f),
    HUSK(47, 1.8f),
    WITHER_SKELETON(48, 3.5f),
    GUARDIAN(49, 2.4f),
    ELDER_GUARDIAN(50, 1.9f),
    NPC(51, 0f),
    WITHER(52, 3.5f),
    ENDER_DRAGON(53, 0f),
    SHULKER(54, 0f),
    ENDERMITE(55, 0f),
    AGENT(56, 0f),
    VINDICATOR(57, 0f),



    ARMOR_STAND(61, 0f),
    TRIPOD_CAMERA(62, 0f),
    PLAYER(63, 1.8f, 0.6f, 0.6f, 1.62f),
    ITEM(64, 0.25f, 0.25f, 0.25f, 0.125f),
    PRIMED_TNT(65, 0.98f, 0.98f, 0.98f, 0.49f),
    FALLING_SAND(66, 0.98f, 0.98f, 0.98f, 0.49f),
    MOVING_BLOCK(67, 0f),
    XP_BOTTLE(68, 0f),
    XP_ORB(69, 0f),
    EYE_OF_ENDER(70, 0f),
    ENDER_CRYSTAL(71, 0f),
    FIREWORKS_ROCKET(72, 0f),



    SHULKER_BULLET(76, 0f),
    FISHING_HOOK(77, 0f),
    CHALKBOARD(78, 0f),
    DRAGON_FIREBALL(79, 0f),
    ARROW(80, 0.5f, 0.5f, 0.5f, 0.25f),
    SNOWBALL(81, 0f),
    EGG(82, 0f),
    PAINTING(83, 0f),
    MINECART(84, 0f),
    LARGE_FIREBALL(85, 0f),
    SPLASH_POTION(86, 0f),
    ENDER_PEARL(87, 0f),
    LEASH_FENCE_KNOT(88, 0f),
    WITHER_SKULL(89, 0f),
    BOAT(90, 0f),
    WITHER_SKULL_DANGEROUS(91, 0f),
    LIGHTNING_BOLT(93, 0f),
    SMALL_FIREBALL(94, 0f),
    AREA_EFFECT_CLOUD(95, 0f),
    HOPPER_MINECART(96, 0f),
    TNT_MINECART(97, 0f),
    CHEST_MINECART(98, 0f),

    COMMAND_BLOCK_MINECART(100, 0f),
    LINGERING_POTION(101, 0f),
    LLAMA_SPIT(102, 0f),
    EVOCATION_FANG(103, 0f),
    EVOCATION_ILLAGER(104, 0f),
    VEX(105, 0f);


    private final int type;
    private final float height;
    private final float depth;
    private final float width;
    private final float offset;

    EntityTypeData(int type, float height) {
        this(type, height, 0f, 0f, height);
    }

    EntityTypeData(int type, float height, float depth, float width, float offset) {
        this.type = type;
        this.height = height;
        this.depth = depth;
        this.width = width;
        this.offset = offset;
    }

    public int getType() {
        return type;
    }

    public float getHeight() {
        return height;
    }

    public float getDepth() {
        return depth;
    }

    public float getWidth() {
        return width;
    }

    public float getOffset() {
        return offset;
    }
}
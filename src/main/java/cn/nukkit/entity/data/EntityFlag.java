package cn.nukkit.entity.data;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public enum EntityFlag {
    ON_FIRE(0),
    SNEAKING(1),
    RIDING(2),
    SPRINTING(3),
    ACTION(4),
    INVISIBLE(5),
    TEMPTED(6),
    INLOVE(7),
    SADDLED(8),
    POWERED(9),
    IGNITED(10),
    BABY(11), //disable head scaling
    CONVERTING(12),
    CRITICAL(13),
    CAN_SHOW_NAMETAG(14),
    ALWAYS_SHOW_NAMETAG(15),
    IMMOBILE(16),
    SILENT(17),
    WALLCLIMBING(18),
    CAN_CLIMB(19),
    SWIMMER(20),
    CAN_FLY(21),
    WALKER(22),
    RESTING(23),
    SITTING(24),
    ANGRY(25),
    INTERESTED(26),
    CHARGED(27),
    TAMED(28),
    ORPHANED(29),
    LEASHED(30),
    SHEARED(31),
    GLIDING(32),
    ELDER(33),
    MOVING(34),
    BREATHING(35),
    CHESTED(36),
    STACKABLE(37),
    SHOWBASE(38),
    REARING(39),
    VIBRATING(40),
    IDLING(41),
    EVOKER_SPELL(42),
    CHARGE_ATTACK(43),
    WASD_CONTROLLED(44),
    CAN_POWER_JUMP(45),
    LINGER(46),
    HAS_COLLISION(47),
    GRAVITY(48),
    FIRE_IMMUNE(49),
    DANCING(50),
    ENCHANTED(51),
    SHOW_TRIDENT_ROPE(52), // tridents show an animated rope when enchanted with loyalty after they are thrown and return to their owner. To be combined with DATA_OWNER_EID
    CONTAINER_PRIVATE(53), //inventory is private, doesn't drop contents when killed if true
    //public static final int TransformationComponent 54; ???
    SPIN_ATTACK(55),
    SWIMMING(56),
    BRIBED(57), //dolphins have this set when they go to find treasure for the player
    PREGNANT(58),
    LAYING_EGG(59);

    private static final Int2ObjectMap<EntityFlag> ID_MAP = new Int2ObjectOpenHashMap<>();

    static {
        for (EntityFlag flag : values()) {
            ID_MAP.put(flag.id, flag);
        }
    }

    private final int id;

    EntityFlag(int id) {
        this.id = id;
    }

    public static EntityFlag from(int id) {
        return ID_MAP.get(id);
    }

    public int getId() {
        return id;
    }
}

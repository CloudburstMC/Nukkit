package cn.nukkit.entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public enum EffectInfo {
    SPEED(1),
    SLOWNESS(2),
    HASTE(3),
    SWIFTNESS(3),
    FATIGUE(4),
    MINING_FATIGUE(4),
    STRENGTH(5),
    //TODO:  HEALING(6),
    //TODO:  HARMING(7),
    JUMP(8),
    NAUSEA(9),
    CONFUSION(9),
    REGENERATION(10),
    DAMAGE_RESISTANCE(11),
    FIRE_RESISTANCE(12),
    WATER_BREATHING(13),
    INVISIBILITY(14),
    // BLINDNESS(15),
    // NIGHT_VISION(16),
    // HUNGER(17),
    WEAKNESS(18),
    POISON(19),
    WITHER(20),
    HEALTH_BOOST(21);

    private final int id;

    EffectInfo(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

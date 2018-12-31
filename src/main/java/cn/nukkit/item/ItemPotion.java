package cn.nukkit.item;

public class ItemPotion extends Item {

    public static final int NO_EFFECTS = 0;
    public static final int MUNDANE = 1;
    public static final int MUNDANE_II = 2;
    public static final int THICK = 3;
    public static final int AWKWARD = 4;
    public static final int NIGHT_VISION = 5;
    public static final int NIGHT_VISION_LONG = 6;
    public static final int INVISIBLE = 7;
    public static final int INVISIBLE_LONG = 8;
    public static final int LEAPING = 9;
    public static final int LEAPING_LONG = 10;
    public static final int LEAPING_II = 11;
    public static final int FIRE_RESISTANCE = 12;
    public static final int FIRE_RESISTANCE_LONG = 13;
    public static final int SPEED = 14;
    public static final int SPEED_LONG = 15;
    public static final int SPEED_II = 16;
    public static final int SLOWNESS = 17;
    public static final int SLOWNESS_LONG = 18;
    public static final int WATER_BREATHING = 19;
    public static final int WATER_BREATHING_LONG = 20;
    public static final int INSTANT_HEALTH = 21;
    public static final int INSTANT_HEALTH_II = 22;
    public static final int HARMING = 23;
    public static final int HARMING_II = 24;
    public static final int POISON = 25;
    public static final int POISON_LONG = 26;
    public static final int POISON_II = 27;
    public static final int REGENERATION = 28;
    public static final int REGENERATION_LONG = 29;
    public static final int REGENERATION_II = 30;
    public static final int STRENGTH = 31;
    public static final int STRENGTH_LONG = 32;
    public static final int STRENGTH_II = 33;
    public static final int WEAKNESS = 34;
    public static final int WEAKNESS_LONG = 35;
    public static final int DECAY = 36;

    public ItemPotion() {
        this(0, 1);
    }

    public ItemPotion(Integer meta) {
        this(meta, 1);
    }

    public ItemPotion(Integer meta, int count) {
        super(POTION, meta, count, "Potion");
    }

}
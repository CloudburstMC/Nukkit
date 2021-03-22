package cn.nukkit.entity;
/**
 * Attribute
 *
 * @author Box, MagicDroidX(code), PeratX @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.ServerException;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ToString
public class Attribute implements Cloneable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final Attribute[] EMPTY_ARRAY = new Attribute[0];

    public static final int ABSORPTION = 0;
    public static final int SATURATION = 1;
    public static final int EXHAUSTION = 2;
    public static final int KNOCKBACK_RESISTANCE = 3;
    public static final int MAX_HEALTH = 4;
    public static final int MOVEMENT_SPEED = 5;
    public static final int FOLLOW_RANGE = 6;
    public static final int MAX_HUNGER = 7;
    public static final int FOOD = 7;
    public static final int ATTACK_DAMAGE = 8;
    public static final int EXPERIENCE_LEVEL = 9;
    public static final int EXPERIENCE = 10;
    public static final int LUCK = 11;

    protected static Map<Integer, Attribute> attributes = new HashMap<>();

    protected float minValue;
    protected float maxValue;
    protected float defaultValue;
    protected float currentValue;
    protected String name;
    protected boolean shouldSend;
    private int id;

    private Attribute(int id, String name, float minValue, float maxValue, float defaultValue, boolean shouldSend) {
        this.id = id;
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.shouldSend = shouldSend;
        this.currentValue = this.defaultValue;
    }

    public static void init() {
        addAttribute(ABSORPTION, "minecraft:absorption", 0.00f, 340282346638528859811704183484516925440.00f, 0.00f);
        addAttribute(SATURATION, "minecraft:player.saturation", 0.00f, 20.00f, 5.00f);
        addAttribute(EXHAUSTION, "minecraft:player.exhaustion", 0.00f, 5.00f, 0.41f);
        addAttribute(KNOCKBACK_RESISTANCE, "minecraft:knockback_resistance", 0.00f, 1.00f, 0.00f);
        addAttribute(MAX_HEALTH, "minecraft:health", 0.00f, 20.00f, 20.00f);
        addAttribute(MOVEMENT_SPEED, "minecraft:movement", 0.00f, 340282346638528859811704183484516925440.00f, 0.10f);
        addAttribute(FOLLOW_RANGE, "minecraft:follow_range", 0.00f, 2048.00f, 16.00f, false);
        addAttribute(MAX_HUNGER, "minecraft:player.hunger", 0.00f, 20.00f, 20.00f);
        addAttribute(ATTACK_DAMAGE, "minecraft:attack_damage", 0.00f, 340282346638528859811704183484516925440.00f, 1.00f, false);
        addAttribute(EXPERIENCE_LEVEL, "minecraft:player.level", 0.00f, 24791.00f, 0.00f);
        addAttribute(EXPERIENCE, "minecraft:player.experience", 0.00f, 1.00f, 0.00f);
        addAttribute(LUCK, "minecraft:luck", -1024, 1024, 0);
    }

    public static Attribute addAttribute(int id, String name, float minValue, float maxValue, float defaultValue) {
        return addAttribute(id, name, minValue, maxValue, defaultValue, true);
    }

    public static Attribute addAttribute(int id, String name, float minValue, float maxValue, float defaultValue, boolean shouldSend) {
        if (minValue > maxValue || defaultValue > maxValue || defaultValue < minValue) {
            throw new IllegalArgumentException("Invalid ranges: min value: " + minValue + ", max value: " + maxValue + ", defaultValue: " + defaultValue);
        }

        return attributes.put(id, new Attribute(id, name, minValue, maxValue, defaultValue, shouldSend));
    }

    public static Attribute getAttribute(int id) {
        if (attributes.containsKey(id)) {
            return attributes.get(id).clone();
        }
        throw new ServerException("Attribute id: " + id + " not found");
    }

    /**
     * @param name name
     * @return null|Attribute
     */
    public static Attribute getAttributeByName(String name) {
        for (Attribute a : attributes.values()) {
            if (Objects.equals(a.getName(), name)) {
                return a.clone();
            }
        }
        return null;
    }

    public float getMinValue() {
        return this.minValue;
    }

    public Attribute setMinValue(float minValue) {
        if (minValue > this.getMaxValue()) {
            throw new IllegalArgumentException("Value " + minValue + " is bigger than the maxValue!");
        }
        this.minValue = minValue;
        return this;
    }

    public float getMaxValue() {
        return this.maxValue;
    }

    public Attribute setMaxValue(float maxValue) {
        if (maxValue < this.getMinValue()) {
            throw new IllegalArgumentException("Value " + maxValue + " is bigger than the minValue!");
        }
        this.maxValue = maxValue;
        return this;
    }

    public float getDefaultValue() {
        return this.defaultValue;
    }

    public Attribute setDefaultValue(float defaultValue) {
        if (defaultValue > this.getMaxValue() || defaultValue < this.getMinValue()) {
            throw new IllegalArgumentException("Value " + defaultValue + " exceeds the range!");
        }
        this.defaultValue = defaultValue;
        return this;
    }

    public float getValue() {
        return this.currentValue;
    }

    public Attribute setValue(float value) {
        return setValue(value, true);
    }

    public Attribute setValue(float value, boolean fit) {
        if (value > this.getMaxValue() || value < this.getMinValue()) {
            if (!fit) {
                throw new IllegalArgumentException("Value " + value + " exceeds the range!");
            }
            value = Math.min(Math.max(value, this.getMinValue()), this.getMaxValue());
        }
        this.currentValue = value;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public boolean isSyncable() {
        return this.shouldSend;
    }

    @Override
    public Attribute clone() {
        try {
            return (Attribute) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}

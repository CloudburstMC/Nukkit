package cn.nukkit.entity;
/**
 * Attribute
 *
 * @author Box, MagicDroidX(code), PeratX @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Attribute implements Cloneable {
    public static final int MAX_HEALTH = 0;
    public static final int EXPERIENCE = 1;
    public static final int EXPERIENCE_LEVEL = 2;
    public static final int MAX_HUNGER = 3;
    public static final int MOVEMENT_SPEED = 4;

    private int id;
    protected float minValue;
    protected float maxValue;
    protected float defaultValue;
    protected float currentValue;
    protected String name;
    protected boolean shouldSend;


    protected static Map<Integer, Attribute> attributes = new HashMap<>();

    public static void init() {
        addAttribute(MAX_HEALTH, "generic.health", 0, 0x7fffffff, 20, true);
        addAttribute(EXPERIENCE, "player.experience", 0, 1, 0, true);
        addAttribute(EXPERIENCE_LEVEL, "player.level", 0, 24791, 0, true);
        addAttribute(MAX_HUNGER, "player.hunger", 0, 20, 20, true);
        addAttribute(MOVEMENT_SPEED, "generic.movementSpeed", 0, 24791, 0.1f, true);
    }

    public static Attribute addAttribute(int id, String name, float minValue, float maxValue, float defaultValue, boolean shouldSend) {
        if (minValue > maxValue || defaultValue > maxValue || defaultValue < minValue) {
            throw new IllegalArgumentException("Invalid ranges: min value: " + minValue + ", max value: " + maxValue + ", defaultValue: " + defaultValue);
        }

        return attributes.put(id, new Attribute(id, name, minValue, maxValue, defaultValue, shouldSend));
    }

    public static Attribute getAttribute(int id) {
        return attributes.containsKey(id) ? attributes.get(id).clone() : null;
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

    private Attribute(int id, String name, float minValue, float maxValue, float defaultValue, boolean shouldSend) {
        this.id = id;
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.shouldSend = shouldSend;

        this.currentValue = this.defaultValue;
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
        if (value > this.getMaxValue() || value < this.getMinValue()) {
            throw new IllegalArgumentException("Value " + value + " exceeds the range!");
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


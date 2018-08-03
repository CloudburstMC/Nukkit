package com.nukkitx.api.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Documentation by fromgate on 26.04.2016.
 */
public interface ConfigSection {

    /**
     * Get root section as LinkedHashMap
     *
     * @return root section
     */
    Map<String, Object> getAllMap();

    /**
     * Get new instance of config section
     *
     * @return instance
     */
    ConfigSection getAll();

    /**
     * Get object by key. If section does not contain value, return null
     *
     * @param key key of object
     * @return value
     */
    Object get(String key);

    /**
     * Get object by key. If section does not contain value, return default value
     *
     * ll
     * @param key key of object
     * @param defaultValue value if null
     * @return value
     */
    <T> T get(String key, T defaultValue);

    /**
     * Store value into config section
     *
     * @param key key of object
     * @param value value to set
     */
    void set(String key, Object value);

    /**
     * Check type of section element defined by key. Return true this element is ConfigSection
     *
     * @param key key of section
     * @return is section
     */
    boolean isSection(String key);

    /**
     * Get config section element defined by key
     *
     * @param key key of section
     * @return section
     */
    ConfigSection getSection(String key);

    /**
     * Get all ConfigSections in root path.
     * Example config:
     *  a1:
     *    b1:
     *      c1:
     *      c2:
     *  a2:
     *    b2:
     *      c3:
     *      c4:
     *  a3: true
     *  a4: "hello"
     *  a5: 100
     * <p>
     * getSections() will return new ConfigSection, that contains sections a1 and a2 only.
     *
     * @return sections
     */
    ConfigSection getSections();

    /**
     * Get sections (and only sections) from provided path
     *
     * @param key - config section path, if null or empty root path will used.
     * @return sections
     */
    ConfigSection getSections(String key);

    /**
     * Get int value of config section element
     *
     * @param key - key (inside) current section (default value equals to 0)
     * @return value
     */
    int getInt(String key);

    /**
     * Get int value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return value
     */
    int getInt(String key, int defaultValue);

    /**
     * Check type of section element defined by key. Return true this element is Integer
     *
     * @param key          - key (inside) current section
     * @return is value of integer type
     */
    boolean isInt(String key);

    /**
     * Get long value of config section element
     *
     * @param key - key (inside) current section
     * @return value
     */
    long getLong(String key);

    /**
     * Get long value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return value
     */
    long getLong(String key, long defaultValue);

    /**
     * Check type of section element defined by key. Return true this element is Long
     *
     * @param key          - key (inside) current section
     * @return is value long
     */
    boolean isLong(String key);

    /**
     * Get double value of config section element
     *
     * @param key - key (inside) current section
     * @return value
     */
    double getDouble(String key);

    /**
     * Get double value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return value
     */
    double getDouble(String key, double defaultValue);

    /**
     * Check type of section element defined by key. Return true this element is Double
     *
     * @param key          - key (inside) current section
     * @return is value double
     */
    boolean isDouble(String key);

    /**
     * Get String value of config section element
     *
     * @param key - key (inside) current section
     * @return value
     */
    String getString(String key);

    /**
     * Get String value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return value
     */
    String getString(String key, String defaultValue);

    /**
     * Check type of section element defined by key. Return true this element is String
     *
     * @param key          - key (inside) current section
     * @return is value String
     */
    boolean isString(String key);

    /**
     * Get boolean value of config section element
     *
     * @param key - key (inside) current section
     * @return value
     */
    boolean getBoolean(String key);

    /**
     * Get boolean value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return value
     */
    boolean getBoolean(String key, boolean defaultValue);

    /**
     * Check type of section element defined by key. Return true this element is Integer
     *
     * @param key          - key (inside) current section
     * @return is value Boolean
     */
    boolean isBoolean(String key);

    /**
     * Get List value of config section element
     *
     * @param key - key (inside) current section
     * @return value
     */
    List getList(String key);

    /**
     * Get List value of config section element
     *
     * @param key         - key (inside) current section
     * @param defaultList - default value that will returned if section element is not exists
     * @return value
     */
    List getList(String key, List defaultList);

    /**
     * Check type of section element defined by key. Return true this element is List
     *
     * @param key        - key (inside) current section
     * @return is value list
     */
    boolean isList(String key);

    /**
     * Get String List value of config section element
     *
     * @param key - key (inside) current section
     * @return value
     */
    List<String> getStringList(String key);

    /**
     * Get Integer List value of config section element
     *
     * @param key - key (inside) current section
     * @return value
     */
    List<Integer> getIntegerList(String key);

    /**
     * Get Boolean List value of config section element
     *
     * @param key - key (inside) current section
     * @return value
     */
    List<Boolean> getBooleanList(String key);

    /**
     * Get Double List value of config section element
     *
     * @param key - key (inside) current section
     * @return value
     */
    List<Double> getDoubleList(String key);

    /**
     * Get Float List value of config section element
     *
     * @param key - key (inside) current section
     * @return value
     */
    List<Float> getFloatList(String key);

    /**
     * Get Long List value of config section element
     *
     * @param key - key (inside) current section
     * @return value
     */
    List<Long> getLongList(String key);

    /**
     * Get Byte List value of config section element
     *
     * @param key - key (inside) current section
     * @return value
     */
    List<Byte> getByteList(String key);

    /**
     * Get Character List value of config section element
     *
     * @param key - key (inside) current section
     * @return value
     */
    List<Character> getCharacterList(String key);

    /**
     * Get Short List value of config section element
     *
     * @param key - key (inside) current section
     * @return value
     */
    List<Short> getShortList(String key);

    /**
     * Get Map List value of config section element
     *
     * @param key - key (inside) current section
     * @return value
     */
    List<Map> getMapList(String key);

    /**
     * Check existence of config section element
     *
     * @param key         - key (inside) current section
     * @param ignoreCase ignore key case sensitivity
     * @return value exists
     */
    boolean exists(String key, boolean ignoreCase);

    /**
     * Check existence of config section element
     *
     * @param key        - key (inside) current section
     * @return value exists
     */
    boolean exists(String key);

    /**
     * Remove config section element
     *
     * @param key key to remove
     */
    void remove(String key);

    /**
     * Get all keys
     *
     * @param child - true = include child keys
     * @return keys
     */
    Set<String> getKeys(boolean child);

    /**
     * Get all keys
     *
     * @return keys
     */
    Set<String> getKeys();
}

/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.api.util;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Config {

    void reload();

    boolean load(String file);

    boolean load(String file, Config.Type type);

    boolean load(String file, Config.Type type, ConfigSection defaultMap);

    boolean load(InputStream inputStream);

    boolean check();

    boolean isCorrect();

    boolean save(File file, boolean async);

    boolean save(File file);

    boolean save();

    boolean save(boolean async);

    void set(final String key, Object value);

    Object get(String key);

    <T> T get(String key, T defaultValue);

    ConfigSection getSection(String key);

    boolean isSection(String key);

    ConfigSection getSections(String key);

    ConfigSection getSections();

    int getInt(String key);

    int getInt(String key, int defaultValue);

    boolean isInt(String key);

    long getLong(String key);

    long getLong(String key, long defaultValue);

    boolean isLong(String key);

    double getDouble(String key);

    double getDouble(String key, double defaultValue);

    boolean isDouble(String key);

    String getString(String key);

    String getString(String key, String defaultValue);

    boolean isString(String key);

    boolean getBoolean(String key);

    boolean getBoolean(String key, boolean defaultValue);

    boolean isBoolean(String key);

    List getList(String key);

    List getList(String key, List defaultList);

    boolean isList(String key);

    List<String> getStringList(String key);

    List<Integer> getIntegerList(String key);

    List<Boolean> getBooleanList(String key);

    List<Double> getDoubleList(String key);

    List<Float> getFloatList(String key);

    List<Long> getLongList(String key);

    List<Byte> getByteList(String key);

    List<Character> getCharacterList(String key);

    List<Short> getShortList(String key);

    List<Map> getMapList(String key);

    void setAll(LinkedHashMap<String, Object> map);

    void setAll(ConfigSection section);

    boolean exists(String key);

    boolean exists(String key, boolean ignoreCase);

    void remove(String key);

    /**
     * Get root (main) config section of the Config
     *
     * @return
     */
    ConfigSection getRootSection();

    int setDefault(LinkedHashMap<String, Object> map);

    int setDefault(ConfigSection map);

    /**
     * @deprecated use {@link #get(String)} instead
     */
    @Deprecated
    Object getNested(String key);

    /**
     * @deprecated use {@link #get(String, T)} instead
     */
    @Deprecated
    <T> T getNested(String key, T defaultValue);

    /**
     * @deprecated use {@link #get(String)} instead
     */
    @Deprecated
    <T> T getNestedAs(String key, Class<T> type);

    /**
     * @deprecated use {@link #remove(String)} instead
     */
    @Deprecated
    void removeNested(String key);

    Set<String> getKeys();

    Set<String> getKeys(boolean child);

    enum Type {
        DETECT,
        PROPERTIES,
        CNF,
        JSON,
        YAML,
        ENUM,
        ENUMERATION,
    }
}

package cn.nukkit.level.gamerule;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

public class GameRuleMap implements Iterable<Map.Entry<GameRule, Object>> {
    private final Map<GameRule, Object> gameRules = new HashMap<>();
    private volatile boolean dirty;

    public GameRuleMap() {
    }

    public GameRuleMap(GameRuleMap gameRuleMap) {
        Preconditions.checkNotNull(gameRuleMap, "gameRuleMap");
        this.putAll(gameRuleMap);
    }

    public void putAll(GameRuleMap gameRuleMap) {
        Preconditions.checkNotNull(gameRuleMap, "gameRuleMap");
        this.gameRules.putAll(gameRuleMap.gameRules);
    }

    public boolean contains(GameRule<?> gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return this.gameRules.containsKey(gameRule);
    }

    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> T get(GameRule<T> gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return (T) this.gameRules.get(gameRule);
    }

    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> T put(GameRule<T> gameRule, T value) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        Preconditions.checkNotNull(value, "value");
        T oldValue = (T) this.gameRules.put(gameRule, value);
        if (oldValue != value) {
            this.dirty = true;
        }
        return oldValue;
    }

    public void forEach(BiConsumer<GameRule, Object> consumer) {
        this.gameRules.forEach(consumer);
    }

    public int size() {
        return this.gameRules.size();
    }

    public boolean isDirty() {
        return dirty;
    }

    public void refresh() {
        this.dirty = false;
    }

    @Override
    public Iterator<Map.Entry<GameRule, Object>> iterator() {
        return this.gameRules.entrySet().iterator();
    }

    @Override
    public String toString() {
        return this.gameRules.toString();
    }

    @Override
    public int hashCode() {
        return this.gameRules.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != getClass()) return false;
        GameRuleMap that = (GameRuleMap) obj;
        return this.gameRules.equals(that.gameRules);
    }
}

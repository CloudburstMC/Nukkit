package com.nukkitx.server.level;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.nukkitx.api.level.GameRules;
import com.nukkitx.api.level.data.GameRule;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.nukkitx.api.level.data.GameRule.*;

@Log4j2
@ParametersAreNonnullByDefault
public class NukkitGameRules implements GameRules {
    private final Map<String, com.nukkitx.protocol.bedrock.data.GameRule> gameRules = new ConcurrentHashMap<>();
    private volatile boolean stale;

    private NukkitGameRules() {
    }


    public static NukkitGameRules newGameRules() {
        NukkitGameRules gameRules = new NukkitGameRules();

        gameRules.setGameRule(COMMAND_BLOCK_OUTPUT, true);
        gameRules.setGameRule(DO_DAYLIGHT_CYCLE, true);
        gameRules.setGameRule(DO_ENTITY_DROPS, true);
        gameRules.setGameRule(DO_FIRE_TICK, true);
        gameRules.setGameRule(DO_MOB_LOOT, true);
        gameRules.setGameRule(DO_MOB_SPAWNING, true);
        gameRules.setGameRule(DO_TILE_DROPS, true);
        gameRules.setGameRule(DO_WEATHER_CYCLE, true);
        gameRules.setGameRule(DROWNING_DAMAGE, true);
        gameRules.setGameRule(FALL_DAMAGE, true);
        gameRules.setGameRule(FIRE_DAMAGE, true);
        gameRules.setGameRule(KEEP_INVENTORY, false);
        gameRules.setGameRule(MOB_GRIEFING, true);
        gameRules.setGameRule(NATURAL_REGENERATION, true);
        gameRules.setGameRule(PVP, true);
        gameRules.setGameRule(SEND_COMMAND_FEEDBACK, true);
        gameRules.setGameRule(SHOW_COORDINATES, false);
        gameRules.setGameRule(TNT_EXPLODES, true);

        return gameRules;
    }

    public Collection<com.nukkitx.protocol.bedrock.data.GameRule> getGameRules() {
        return ImmutableList.copyOf(gameRules.values());
    }

    public boolean isStale() {
        return stale;
    }

    public void refresh() {
        stale = false;
    }

    public <T> void setGameRule(String rule, T value) {
        Preconditions.checkNotNull(rule, "rule");
        Preconditions.checkNotNull(value, "value");
        com.nukkitx.protocol.bedrock.data.GameRule gameRule = gameRules.get(rule);
        checkValue(gameRule, value.getClass());
        if (gameRule == null || !value.equals(gameRule.getValue())) {
            gameRules.put(rule, new com.nukkitx.protocol.bedrock.data.GameRule<>(rule, value));
            stale = true;
        }
    }

    @Override
    public void setGameRule(GameRule rule, boolean value) {
        Preconditions.checkNotNull(rule, "rule");
        setGameRule(rule.getName(), value);
    }

    @Override
    public void setGameRule(GameRule rule, int value) {
        Preconditions.checkNotNull(rule, "rule");
        setGameRule(rule.getName(), value);
    }

    @Override
    public void setGameRule(@Nonnull GameRule rule, float value) {
        Preconditions.checkNotNull(rule, "rule");
        setGameRule(rule.getName(), value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String rule, Class<T> valueClass) {
        Preconditions.checkNotNull(rule, "rule");
        com.nukkitx.protocol.bedrock.data.GameRule gameRule = gameRules.get(rule);
        checkValue(gameRule, valueClass);
        return gameRule == null ? null : (T) gameRule.getValue();
    }

    @Override
    public boolean getBoolean(GameRule rule) {
        Preconditions.checkNotNull(rule, "rule");
        Boolean value = get(rule.getName(), Boolean.class);
        return value == null ? false : value;
    }

    @Override
    public int getInteger(GameRule rule) {
        Preconditions.checkNotNull(rule, "rule");
        Integer value = get(rule.getName(), Integer.class);
        return value == null ? 0 : value;
    }

    @Override
    public float getFloat(GameRule rule) {
        Preconditions.checkNotNull(rule, "rule");
        Float value = get(rule.getName(), Float.class);
        return value == null ? 0f : value;
    }

    @Override
    public boolean contains(GameRule rule) {
        Preconditions.checkNotNull(rule, "rule");
        return gameRules.containsKey(rule.getName());
    }

    @Nonnull
    @Override
    public List<String> getRules() {
        return ImmutableList.copyOf(gameRules.keySet());
    }

    private void checkValue(@Nullable com.nukkitx.protocol.bedrock.data.GameRule gameRule, Class valueClass) {
        if (gameRule != null) {
            if (gameRule.getValue().getClass() != valueClass) {
                throw new ClassCastException("GameRule is not of class " + valueClass.getName());
            }
        }
    }
}

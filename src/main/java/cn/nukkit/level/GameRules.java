package cn.nukkit.level;

import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BinaryStream;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static cn.nukkit.level.GameRule.*;

@SuppressWarnings({"unchecked"})
public class GameRules {
    private final EnumMap<GameRule, Value> gameRules = new EnumMap<>(GameRule.class);
    private boolean stale;

    private GameRules() {
    }


    public static GameRules getDefault() {
        GameRules gameRules = new GameRules();

        gameRules.gameRules.put(COMMAND_BLOCKS_ENABLED, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(COMMAND_BLOCK_OUTPUT, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_DAYLIGHT_CYCLE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_ENTITY_DROPS, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_FIRE_TICK, new Value(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_INSOMNIA, new Value(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_IMMEDIATE_RESPAWN, new Value(Type.BOOLEAN, false));
        gameRules.gameRules.put(DO_MOB_LOOT, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_MOB_SPAWNING, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_TILE_DROPS, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_WEATHER_CYCLE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DROWNING_DAMAGE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(FALL_DAMAGE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(FIRE_DAMAGE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(FREEZE_DAMAGE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(FUNCTION_COMMAND_LIMIT, new Value<>(Type.INTEGER, 10000));
        gameRules.gameRules.put(KEEP_INVENTORY, new Value<>(Type.BOOLEAN, false));
        gameRules.gameRules.put(MAX_COMMAND_CHAIN_LENGTH, new Value<>(Type.INTEGER, 65536));
        gameRules.gameRules.put(MOB_GRIEFING, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(NATURAL_REGENERATION, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(PVP, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(RANDOM_TICK_SPEED, new Value<>(Type.INTEGER, 3));
        gameRules.gameRules.put(SEND_COMMAND_FEEDBACK, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(SHOW_COORDINATES, new Value<>(Type.BOOLEAN, false));
        gameRules.gameRules.put(SHOW_DEATH_MESSAGES, new Value<>(Type.BOOLEAN, true));
        
        // PN: Backward compatibility
        //noinspection deprecation
        gameRules.gameRules.put(SHOW_DEATH_MESSAGE, gameRules.gameRules.get(SHOW_DEATH_MESSAGES));
        
        gameRules.gameRules.put(SPAWN_RADIUS, new Value<>(Type.INTEGER, 5));
        gameRules.gameRules.put(TNT_EXPLODES, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(SHOW_TAGS, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(EXPERIMENTAL_GAMEPLAY, new Value<>(Type.BOOLEAN, false));

        return gameRules;
    }

    public Map<GameRule, Value> getGameRules() {
        return ImmutableMap.copyOf(gameRules);
    }

    public boolean isStale() {
        return stale;
    }

    public void refresh() {
        stale = false;
    }

    public void setGameRule(GameRule gameRule, boolean value) {
        if (!gameRules.containsKey(gameRule)) {
            throw new IllegalArgumentException("Gamerule does not exist");
        }
        gameRules.get(gameRule).setValue(value, Type.BOOLEAN);
        stale = true;
    }

    public void setGameRule(GameRule gameRule, int value) {
        if (!gameRules.containsKey(gameRule)) {
            throw new IllegalArgumentException("Gamerule does not exist");
        }
        gameRules.get(gameRule).setValue(value, Type.INTEGER);
        stale = true;
    }

    public void setGameRule(GameRule gameRule, float value) {
        if (!gameRules.containsKey(gameRule)) {
            throw new IllegalArgumentException("Gamerule does not exist");
        }
        gameRules.get(gameRule).setValue(value, Type.FLOAT);
        stale = true;
    }

    public void setGameRules(GameRule gameRule, String value) throws IllegalArgumentException {
        Preconditions.checkNotNull(gameRule, "gameRule");
        Preconditions.checkNotNull(value, "value");

        switch (getGameRuleType(gameRule)) {
            case BOOLEAN:
                if (value.equalsIgnoreCase("true")) {
                    setGameRule(gameRule, true);
                } else if (value.equalsIgnoreCase("false")) {
                    setGameRule(gameRule, false);
                } else {
                    throw new IllegalArgumentException("Was not a boolean");
                }
                break;
            case INTEGER:
                setGameRule(gameRule, Integer.parseInt(value));
                break;
            case FLOAT:
                setGameRule(gameRule, Float.parseFloat(value));
        }
    }

    public boolean getBoolean(GameRule gameRule) {
        return gameRules.get(gameRule).getValueAsBoolean();
    }

    public int getInteger(GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return gameRules.get(gameRule).getValueAsInteger();
    }

    public float getFloat(GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return gameRules.get(gameRule).getValueAsFloat();
    }

    public String getString(GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return gameRules.get(gameRule).value.toString();
    }

    public Type getGameRuleType(GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return gameRules.get(gameRule).getType();
    }

    public boolean hasRule(GameRule gameRule) {
        return gameRules.containsKey(gameRule);
    }

    public GameRule[] getRules() {
        return gameRules.keySet().toArray(EMPTY_ARRAY);
    }

    // TODO: This needs to be moved out since there is not a separate compound tag in the LevelDB format for Game Rules.
    public CompoundTag writeNBT() {
        CompoundTag nbt = new CompoundTag();

        for (Entry<GameRule, Value> entry : gameRules.entrySet()) {
            nbt.putString(entry.getKey().getName(), entry.getValue().value.toString());
        }

        return nbt;
    }

    public void readNBT(CompoundTag nbt) {
        Preconditions.checkNotNull(nbt);
        for (String key : nbt.getTags().keySet()) {
            Optional<GameRule> gameRule = GameRule.parseString(key);
            if (!gameRule.isPresent()) {
                continue;
            }

            setGameRules(gameRule.get(), nbt.getString(key));
        }
    }

    public enum Type {
        UNKNOWN {
            @Override
            void write(BinaryStream pk, Value value) {
            }
        },
        BOOLEAN {
            @Override
            void write(BinaryStream pk, Value value) {
                pk.putBoolean(value.getValueAsBoolean());
            }
        },
        INTEGER {
            @Override
            void write(BinaryStream pk, Value value) {
                pk.putUnsignedVarInt(value.getValueAsInteger());
            }
        },
        FLOAT {
            @Override
            void write(BinaryStream pk, Value value) {
                pk.putLFloat(value.getValueAsFloat());
            }
        };

        abstract void write(BinaryStream pk, Value value);
    }

    public static class Value<T> {
        private final Type type;
        private T value;
        private boolean canBeChanged;

        public Value(Type type, T value) {
            this.type = type;
            this.value = value;
        }

        private void setValue(T value, Type type) {
            if (this.type != type) {
                throw new UnsupportedOperationException("Rule not of type " + type.name().toLowerCase());
            }
            this.value = value;
        }

        @Since("1.5.0.0-PN")
        public boolean isCanBeChanged() {
            return this.canBeChanged;
        }

        @Since("1.5.0.0-PN")
        public void setCanBeChanged(boolean canBeChanged) {
            this.canBeChanged = canBeChanged;
        }

        public Type getType() {
            return this.type;
        }

        private boolean getValueAsBoolean() {
            if (type != Type.BOOLEAN) {
                throw new UnsupportedOperationException("Rule not of type boolean");
            }
            return (Boolean) value;
        }

        private int getValueAsInteger() {
            if (type != Type.INTEGER) {
                throw new UnsupportedOperationException("Rule not of type integer");
            }
            return (Integer) value;
        }

        private float getValueAsFloat() {
            if (type != Type.FLOAT) {
                throw new UnsupportedOperationException("Rule not of type float");
            }
            return (Float) value;
        }

        @Since("1.5.0.0-PN")
        public void write(BinaryStream stream) {
            stream.putBoolean(this.canBeChanged);
            stream.putUnsignedVarInt(type.ordinal());
            type.write(stream, this);
        }
    }
}

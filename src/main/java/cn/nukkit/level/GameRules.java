package cn.nukkit.level;

import static cn.nukkit.level.GameRule.*;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BinaryStream;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class GameRules {

    private final EnumMap<GameRule, GameRules.Value> gameRules = new EnumMap<>(GameRule.class);

    private boolean stale;

    private GameRules() {
    }

    public static GameRules getDefault() {
        final GameRules gameRules = new GameRules();

        gameRules.gameRules.put(COMMAND_BLOCK_OUTPUT, new GameRules.Value<>(GameRules.Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_DAYLIGHT_CYCLE, new GameRules.Value<>(GameRules.Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_ENTITY_DROPS, new GameRules.Value<>(GameRules.Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_FIRE_TICK, new GameRules.Value(GameRules.Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_IMMEDIATE_RESPAWN, new GameRules.Value(GameRules.Type.BOOLEAN, false));
        gameRules.gameRules.put(DO_MOB_LOOT, new GameRules.Value<>(GameRules.Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_MOB_SPAWNING, new GameRules.Value<>(GameRules.Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_TILE_DROPS, new GameRules.Value<>(GameRules.Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_WEATHER_CYCLE, new GameRules.Value<>(GameRules.Type.BOOLEAN, true));
        gameRules.gameRules.put(DROWNING_DAMAGE, new GameRules.Value<>(GameRules.Type.BOOLEAN, true));
        gameRules.gameRules.put(FALL_DAMAGE, new GameRules.Value<>(GameRules.Type.BOOLEAN, true));
        gameRules.gameRules.put(FIRE_DAMAGE, new GameRules.Value<>(GameRules.Type.BOOLEAN, true));
        gameRules.gameRules.put(KEEP_INVENTORY, new GameRules.Value<>(GameRules.Type.BOOLEAN, false));
        gameRules.gameRules.put(MOB_GRIEFING, new GameRules.Value<>(GameRules.Type.BOOLEAN, true));
        gameRules.gameRules.put(NATURAL_REGENERATION, new GameRules.Value<>(GameRules.Type.BOOLEAN, true));
        gameRules.gameRules.put(PVP, new GameRules.Value<>(GameRules.Type.BOOLEAN, true));
        gameRules.gameRules.put(RANDOM_TICK_SPEED, new GameRules.Value<>(GameRules.Type.INTEGER, 3));
        gameRules.gameRules.put(SEND_COMMAND_FEEDBACK, new GameRules.Value<>(GameRules.Type.BOOLEAN, true));
        gameRules.gameRules.put(SHOW_COORDINATES, new GameRules.Value<>(GameRules.Type.BOOLEAN, false));
        gameRules.gameRules.put(TNT_EXPLODES, new GameRules.Value<>(GameRules.Type.BOOLEAN, true));
        gameRules.gameRules.put(SHOW_DEATH_MESSAGE, new GameRules.Value<>(GameRules.Type.BOOLEAN, true));

        return gameRules;
    }

    public Map<GameRule, GameRules.Value> getGameRules() {
        return ImmutableMap.copyOf(this.gameRules);
    }

    public boolean isStale() {
        return this.stale;
    }

    public void refresh() {
        this.stale = false;
    }

    public void setGameRule(final GameRule gameRule, final boolean value) {
        if (!this.gameRules.containsKey(gameRule)) {
            throw new IllegalArgumentException("Gamerule does not exist");
        }
        this.gameRules.get(gameRule).setValue(value, GameRules.Type.BOOLEAN);
        this.stale = true;
    }

    public void setGameRule(final GameRule gameRule, final int value) {
        if (!this.gameRules.containsKey(gameRule)) {
            throw new IllegalArgumentException("Gamerule does not exist");
        }
        this.gameRules.get(gameRule).setValue(value, GameRules.Type.INTEGER);
        this.stale = true;
    }

    public void setGameRule(final GameRule gameRule, final float value) {
        if (!this.gameRules.containsKey(gameRule)) {
            throw new IllegalArgumentException("Gamerule does not exist");
        }
        this.gameRules.get(gameRule).setValue(value, GameRules.Type.FLOAT);
        this.stale = true;
    }

    public void setGameRules(final GameRule gameRule, final String value) throws IllegalArgumentException {
        Preconditions.checkNotNull(gameRule, "gameRule");
        Preconditions.checkNotNull(value, "value");

        switch (this.getGameRuleType(gameRule)) {
            case BOOLEAN:
                if (value.equalsIgnoreCase("true")) {
                    this.setGameRule(gameRule, true);
                } else if (value.equalsIgnoreCase("false")) {
                    this.setGameRule(gameRule, false);
                } else {
                    throw new IllegalArgumentException("Was not a boolean");
                }
                break;
            case INTEGER:
                this.setGameRule(gameRule, Integer.parseInt(value));
                break;
            case FLOAT:
                this.setGameRule(gameRule, Float.parseFloat(value));
        }
    }

    public boolean getBoolean(final GameRule gameRule) {
        return this.gameRules.get(gameRule).getValueAsBoolean();
    }

    public int getInteger(final GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return this.gameRules.get(gameRule).getValueAsInteger();
    }

    public float getFloat(final GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return this.gameRules.get(gameRule).getValueAsFloat();
    }

    public String getString(final GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return this.gameRules.get(gameRule).value.toString();
    }

    public GameRules.Type getGameRuleType(final GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return this.gameRules.get(gameRule).getType();
    }

    public boolean hasRule(final GameRule gameRule) {
        return this.gameRules.containsKey(gameRule);
    }

    public GameRule[] getRules() {
        return this.gameRules.keySet().toArray(new GameRule[0]);
    }

    // TODO: This needs to be moved out since there is not a separate compound tag in the LevelDB format for Game Rules.
    public CompoundTag writeNBT() {
        final CompoundTag nbt = new CompoundTag();

        for (final Map.Entry<GameRule, GameRules.Value> entry : this.gameRules.entrySet()) {
            nbt.putString(entry.getKey().getName(), entry.getValue().value.toString());
        }

        return nbt;
    }

    public void readNBT(final CompoundTag nbt) {
        Preconditions.checkNotNull(nbt);
        for (final String key : nbt.getTags().keySet()) {
            final Optional<GameRule> gameRule = parseString(key);
            if (!gameRule.isPresent()) {
                continue;
            }

            this.setGameRules(gameRule.get(), nbt.getString(key));
        }
    }

    public enum Type {
        UNKNOWN {
            @Override
            void write(final BinaryStream pk, final GameRules.Value value) {
            }
        },
        BOOLEAN {
            @Override
            void write(final BinaryStream pk, final GameRules.Value value) {
                pk.putBoolean(value.getValueAsBoolean());
            }
        },
        INTEGER {
            @Override
            void write(final BinaryStream pk, final GameRules.Value value) {
                pk.putUnsignedVarInt(value.getValueAsInteger());
            }
        },
        FLOAT {
            @Override
            void write(final BinaryStream pk, final GameRules.Value value) {
                pk.putLFloat(value.getValueAsFloat());
            }
        };

        abstract void write(BinaryStream pk, GameRules.Value value);
    }

    public static class Value<T> {

        private final GameRules.Type type;

        private T value;

        public Value(final GameRules.Type type, final T value) {
            this.type = type;
            this.value = value;
        }

        public GameRules.Type getType() {
            return this.type;
        }

        public void write(final BinaryStream pk) {
            pk.putUnsignedVarInt(this.type.ordinal());
            this.type.write(pk, this);
        }

        private void setValue(final T value, final GameRules.Type type) {
            if (this.type != type) {
                throw new UnsupportedOperationException("Rule not of type " + type.name().toLowerCase());
            }
            this.value = value;
        }

        private boolean getValueAsBoolean() {
            if (this.type != GameRules.Type.BOOLEAN) {
                throw new UnsupportedOperationException("Rule not of type boolean");
            }
            return (Boolean) this.value;
        }

        private int getValueAsInteger() {
            if (this.type != GameRules.Type.INTEGER) {
                throw new UnsupportedOperationException("Rule not of type integer");
            }
            return (Integer) this.value;
        }

        private float getValueAsFloat() {
            if (this.type != GameRules.Type.FLOAT) {
                throw new UnsupportedOperationException("Rule not of type float");
            }
            return (Float) this.value;
        }

    }

}

package cn.nukkit.server.level;

import cn.nukkit.api.level.GameRules;
import cn.nukkit.api.util.data.GameRule;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

import static cn.nukkit.api.util.data.GameRule.*;
import static cn.nukkit.server.nbt.util.VarInt.writeSignedInt;

@SuppressWarnings({"unchecked"})
public class NukkitGameRules implements GameRules {
    private final EnumMap<GameRule, Value> gameRules = new EnumMap<>(GameRule.class);
    private boolean stale;

    private NukkitGameRules() {
    }


    public static NukkitGameRules getDefault() {
        NukkitGameRules nukkitGameRules = new NukkitGameRules();

        nukkitGameRules.gameRules.put(COMMAND_BLOCK_OUTPUT, new Value<>(RuleType.BOOLEAN, true));
        nukkitGameRules.gameRules.put(DO_DAYLIGHT_CYCLE, new Value<>(RuleType.BOOLEAN, true));
        nukkitGameRules.gameRules.put(DO_ENTITY_DROPS, new Value<>(RuleType.BOOLEAN, true));
        nukkitGameRules.gameRules.put(DO_FIRE_TICK, new Value(RuleType.BOOLEAN, true));
        nukkitGameRules.gameRules.put(DO_MOB_LOOT, new Value<>(RuleType.BOOLEAN, true));
        nukkitGameRules.gameRules.put(DO_MOB_SPAWNING, new Value<>(RuleType.BOOLEAN, true));
        nukkitGameRules.gameRules.put(DO_TILE_DROPS, new Value<>(RuleType.BOOLEAN, true));
        nukkitGameRules.gameRules.put(DO_WEATHER_CYCLE, new Value<>(RuleType.BOOLEAN, true));
        nukkitGameRules.gameRules.put(DROWNING_DAMAGE, new Value<>(RuleType.BOOLEAN, true));
        nukkitGameRules.gameRules.put(FALL_DAMAGE, new Value<>(RuleType.BOOLEAN, true));
        nukkitGameRules.gameRules.put(FIRE_DAMAGE, new Value<>(RuleType.BOOLEAN, true));
        nukkitGameRules.gameRules.put(KEEP_INVENTORY, new Value<>(RuleType.BOOLEAN, false));
        nukkitGameRules.gameRules.put(MOB_GRIEFING, new Value<>(RuleType.BOOLEAN, true));
        nukkitGameRules.gameRules.put(NATURAL_REGENERATION, new Value<>(RuleType.BOOLEAN, true));
        nukkitGameRules.gameRules.put(PVP, new Value<>(RuleType.BOOLEAN, true));
        nukkitGameRules.gameRules.put(SEND_COMMAND_FEEDBACK, new Value<>(RuleType.BOOLEAN, true));
        nukkitGameRules.gameRules.put(SHOW_COORDINATES, new Value<>(RuleType.BOOLEAN, false));
        nukkitGameRules.gameRules.put(TNT_EXPLODES, new Value<>(RuleType.BOOLEAN, true));

        return nukkitGameRules;
    }

    public Map<GameRule, Value> getGameRules() {
        return ImmutableMap.copyOf(gameRules);
    }

    public boolean isStale() {
        return stale;
    }

    @Override
    public void setGameRule(@Nonnull GameRule gameRule, boolean value) {
        Value gamerule = gameRules.get(gameRule);
        if (gamerule != null) {
            gamerule.setValue(value, RuleType.BOOLEAN);
        } else {
            gameRules.put(gameRule, new Value<>(RuleType.BOOLEAN, value));
        }
        stale = true;
    }

    @Override
    public void setGameRule(@Nonnull GameRule gameRule, int value) {
        Value gamerule = gameRules.get(gameRule);
        if (gamerule != null) {
            gamerule.setValue(value, RuleType.INTEGER);
        } else {
            gameRules.put(gameRule, new Value<>(RuleType.BOOLEAN, value));
        }
        stale = true;
    }

    @Override
    public void setGameRule(@Nonnull GameRule gameRule, float value) {
        Value gamerule = gameRules.get(gameRule);
        if (gamerule != null) {
            gamerule.setValue(value, RuleType.FLOAT);
        } else {
            gameRules.put(gameRule, new Value<>(RuleType.BOOLEAN, value));
        }
        stale = true;
    }

    @Override
    public boolean getBoolean(@Nonnull GameRule gameRule) {
        return gameRules.get(gameRule).getValueAsBoolean();
    }

    @Override
    public int getInteger(@Nonnull GameRule gameRule) {
        return gameRules.get(gameRule).getValueAsInteger();
    }

    @Override
    public float getFloat(@Nonnull GameRule gameRule) {
        return gameRules.get(gameRule).getValueAsFloat();
    }

    @Override
    public String getString(@Nonnull GameRule gameRule) {
        return gameRules.get(gameRule).value.toString();
    }

    @Override
    public boolean contains(@Nullable GameRule gameRule) {
        return gameRules.containsKey(gameRule);
    }

    private enum RuleType {
        NONE {
            @Override
            void write(ByteBuf buffer, Value value) {
            }
        },
        BOOLEAN {
            @Override
            void write(ByteBuf buffer, Value value) {
                buffer.writeBoolean(value.getValueAsBoolean());
            }
        },
        INTEGER {
            @Override
            void write(ByteBuf buffer, Value value) {
                writeSignedInt(buffer, value.getValueAsInteger());
            }
        },
        FLOAT {
            @Override
            void write(ByteBuf buffer, Value value) {
                buffer.writeFloatLE(value.getValueAsFloat());
            }
        };

        abstract void write(ByteBuf buffer, Value value);
    }

    @AllArgsConstructor
    public static class Value<T> {
        private final RuleType type;
        private T value;

        private void setValue(T value, RuleType type) {
            if (this.type != type) {
                throw new UnsupportedOperationException("Rule not of type " + type.name().toLowerCase());
            }
            this.value = value;
        }

        public RuleType getType() {
            return type;
        }

        private boolean getValueAsBoolean() {
            if (type != RuleType.BOOLEAN) {
                throw new UnsupportedOperationException("Rule not of type boolean");
            }
            return (boolean) value;
        }

        private int getValueAsInteger() {
            if (type != RuleType.INTEGER) {
                throw new UnsupportedOperationException("Rule not of type integer");
            }
            return (int) value;
        }

        private float getValueAsFloat() {
            if (type != RuleType.FLOAT) {
                throw new UnsupportedOperationException("Rule not of type float");
            }
            return (float) value;
        }

        public void write(ByteBuf buffer) {
            buffer.writeByte(type.ordinal());
            type.write(buffer, this);
        }
    }
}

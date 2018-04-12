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

package cn.nukkit.server.level;

import cn.nukkit.api.level.GameRules;
import cn.nukkit.api.level.data.GameRule;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

import static cn.nukkit.api.level.data.GameRule.*;
import static cn.nukkit.server.nbt.util.VarInt.writeSignedInt;

@SuppressWarnings({"unchecked"})
public class NukkitGameRules implements GameRules {
    private final EnumMap<GameRule, Value> gameRules = new EnumMap<>(GameRule.class);
    private boolean stale;

    private NukkitGameRules() {
    }


    public static NukkitGameRules getDefault() {
        NukkitGameRules nukkitGameRules = new NukkitGameRules();

        nukkitGameRules.gameRules.put(COMMAND_BLOCK_OUTPUT, new Value<>(Type.BOOLEAN, true));
        nukkitGameRules.gameRules.put(DO_DAYLIGHT_CYCLE, new Value<>(Type.BOOLEAN, true));
        nukkitGameRules.gameRules.put(DO_ENTITY_DROPS, new Value<>(Type.BOOLEAN, true));
        nukkitGameRules.gameRules.put(DO_FIRE_TICK, new Value(Type.BOOLEAN, true));
        nukkitGameRules.gameRules.put(DO_MOB_LOOT, new Value<>(Type.BOOLEAN, true));
        nukkitGameRules.gameRules.put(DO_MOB_SPAWNING, new Value<>(Type.BOOLEAN, true));
        nukkitGameRules.gameRules.put(DO_TILE_DROPS, new Value<>(Type.BOOLEAN, true));
        nukkitGameRules.gameRules.put(DO_WEATHER_CYCLE, new Value<>(Type.BOOLEAN, true));
        nukkitGameRules.gameRules.put(DROWNING_DAMAGE, new Value<>(Type.BOOLEAN, true));
        nukkitGameRules.gameRules.put(FALL_DAMAGE, new Value<>(Type.BOOLEAN, true));
        nukkitGameRules.gameRules.put(FIRE_DAMAGE, new Value<>(Type.BOOLEAN, true));
        nukkitGameRules.gameRules.put(KEEP_INVENTORY, new Value<>(Type.BOOLEAN, false));
        nukkitGameRules.gameRules.put(MOB_GRIEFING, new Value<>(Type.BOOLEAN, true));
        nukkitGameRules.gameRules.put(NATURAL_REGENERATION, new Value<>(Type.BOOLEAN, true));
        nukkitGameRules.gameRules.put(PVP, new Value<>(Type.BOOLEAN, true));
        nukkitGameRules.gameRules.put(SEND_COMMAND_FEEDBACK, new Value<>(Type.BOOLEAN, true));
        nukkitGameRules.gameRules.put(SHOW_COORDINATES, new Value<>(Type.BOOLEAN, false));
        nukkitGameRules.gameRules.put(TNT_EXPLODES, new Value<>(Type.BOOLEAN, true));

        return nukkitGameRules;
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

    @Override
    public void setGameRule(@Nonnull GameRule gameRule, boolean value) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        gameRules.get(gameRule).setValue(value, Type.BOOLEAN);
        stale = true;
    }

    @Override
    public void setGameRule(@Nonnull GameRule gameRule, int value) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        gameRules.get(gameRule).setValue(value, Type.INTEGER);
        stale = true;
    }

    @Override
    public void setGameRule(@Nonnull GameRule gameRule, float value) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        gameRules.get(gameRule).setValue(value, Type.FLOAT);
        stale = true;
    }

    @Override
    public boolean getBoolean(@Nonnull GameRule gameRule) {
        return gameRules.get(gameRule).getValueAsBoolean();
    }

    @Override
    public int getInteger(@Nonnull GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return gameRules.get(gameRule).getValueAsInteger();
    }

    @Override
    public float getFloat(@Nonnull GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return gameRules.get(gameRule).getValueAsFloat();
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return gameRules.get(gameRule).value.toString();
    }

    @Override
    public boolean contains(@Nullable GameRule gameRule) {
        return gameRules.containsKey(gameRule);
    }

    @Override
    public GameRule[] getRules() {
        return values();
    }

    @AllArgsConstructor
    public static class Value<T> {
        private final Type type;
        private T value;

        private void setValue(T value, Type type) {
            if (this.type != type) {
                throw new UnsupportedOperationException("Rule not of type " + type.name().toLowerCase());
            }
            this.value = value;
        }

        public Type getType() {
            return type;
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

        public void write(ByteBuf buffer) {
            buffer.writeByte(type.ordinal());
            switch (type) {
                case BOOLEAN:
                    buffer.writeBoolean(getValueAsBoolean());
                    break;
                case INTEGER:
                    writeSignedInt(buffer, getValueAsInteger());
                    break;
                case FLOAT:
                    buffer.writeFloatLE(getValueAsFloat());
                    break;
                default:
                    throw new IllegalStateException("Unknown Gamerule Value type");
            }
        }
    }
}

/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.blockstate;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.api.Unsigned;
import cn.nukkit.blockproperty.BlockProperties;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.NoSuchElementException;

import static cn.nukkit.blockstate.IMutableBlockState.handleUnsupportedStorageType;

/**
 * @author joserobjr
 * @since 2020-10-03
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
public class ZeroMutableBlockState extends MutableBlockState {
    private final BlockState state;
    public ZeroMutableBlockState(int blockId, BlockProperties properties) {
        super(blockId, properties);
        state = BlockState.of(blockId);
    }

    @Override
    public void validate() {
    }

    @Nonnull
    @Override
    public MutableBlockState copy() {
        return this;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setDataStorage(@Nonnegative Number storage) {
        Class<? extends Number> c = storage.getClass();
        int state;
        if (c == Integer.class || c == Short.class || c == Byte.class) {
            state = storage.intValue();
        } else {
            try {
                state = new BigDecimal(storage.toString()).intValueExact();
            } catch (ArithmeticException | NumberFormatException e) {
                throw handleUnsupportedStorageType(getBlockId(), storage, e);
            }
        }
        if (state != 0) {
            throw handleUnsupportedStorageType(getBlockId(), storage, new ArithmeticException("ZeroMutableBlockState only accepts zero"));
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setDataStorageFromInt(@Nonnegative int storage) {
        if (storage != 0) {
            throw handleUnsupportedStorageType(getBlockId(), storage, new ArithmeticException("ZeroMutableBlockState only accepts zero"));
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setPropertyValue(String propertyName, @Nullable Serializable value) {
        throw new NoSuchElementException("ZeroMutableBlockState can't have properties. Attempted to set "+propertyName+" to "+value);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setBooleanValue(String propertyName, boolean value) {
        throw new NoSuchElementException("ZeroMutableBlockState can't have properties. Attempted to set "+propertyName+" to "+value);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setIntValue(String propertyName, int value) {
        throw new NoSuchElementException("ZeroMutableBlockState can't have properties. Attempted to set "+propertyName+" to "+value);
    }

    @Nonnegative
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Number getDataStorage() {
        return 0;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isDefaultState() {
        return true;
    }

    @Nonnegative
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getLegacyDamage() {
        return 0;
    }

    @Unsigned
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getBigDamage() {
        return 0;
    }

    @Nonnegative
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BigInteger getHugeDamage() {
        return BigInteger.ZERO;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Serializable getPropertyValue(String propertyName) {
        throw new NoSuchElementException("ZeroMutableBlockState can't have properties. Attempted get property "+propertyName);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getIntValue(String propertyName) {
        throw new NoSuchElementException("ZeroMutableBlockState can't have properties. Attempted get property "+propertyName);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean getBooleanValue(String propertyName) {
        throw new NoSuchElementException("ZeroMutableBlockState can't have properties. Attempted get property "+propertyName);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public String getPersistenceValue(String propertyName) {
        throw new NoSuchElementException("ZeroMutableBlockState can't have properties. Attempted get property "+propertyName);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockState getCurrentState() {
        return state;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getExactIntStorage() {
        return 0;
    }
}

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

import cn.nukkit.api.*;
import cn.nukkit.block.Block;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyException;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.Validation;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import static cn.nukkit.api.API.Definition.INTERNAL;
import static cn.nukkit.api.API.Usage.INCUBATING;
import static cn.nukkit.blockstate.IMutableBlockState.handleUnsupportedStorageType;

/**
 * @author joserobjr
 * @since 2020-10-03
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ParametersAreNonnullByDefault
public class ByteMutableBlockState extends MutableBlockState {
    private byte storage;
    
    public ByteMutableBlockState(int blockId, BlockProperties properties, byte state) {
        super(blockId, properties);
        this.storage = state;
    }
    
    public ByteMutableBlockState(int blockId, BlockProperties properties) {
        this(blockId, properties, (byte)0);
    }

    @Nonnegative
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    public int getLegacyDamage() {
        return storage & Block.DATA_MASK;
    }

    @Unsigned
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    public int getBigDamage() {
        return storage;
    }

    @Nonnegative
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public BigInteger getHugeDamage() {
        return BigInteger.valueOf(storage);
    }

    @Nonnegative
    @Nonnull
    @Override
    public Byte getDataStorage() {
        return storage;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isDefaultState() {
        return storage == 0;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setDataStorage(@Nonnegative Number storage) {
        Class<? extends Number> c = storage.getClass();
        byte state;
        if (c == Byte.class) {
            state = storage.byteValue();
        } else {
            try {
                state = new BigDecimal(storage.toString()).byteValueExact();
            } catch (ArithmeticException | NumberFormatException e) {
                throw handleUnsupportedStorageType(getBlockId(), storage, e);
            }
        }
        validate();
        setDataStorageFromInt(state);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setDataStorageFromInt(@Nonnegative int storage) {
        validate(storage);
        this.storage = (byte)storage;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    @API(definition = INTERNAL, usage = INCUBATING)
    void setDataStorageWithoutValidation(Number storage) {
        this.storage = storage.byteValue();
    }

    @Override
    public void validate() {
        validate(storage);
    }
    
    private void validate(int state) {
        if (state == 0) {
            return;
        }

        Validation.checkPositive("state", state);
        
        if (state < 0 || state > Byte.MAX_VALUE) {
            throw new InvalidBlockStateException(BlockState.of(getBlockId(), state), 
                    "The state have more bits than the storage space. Storage: Byte, Property Bits: "+properties.getBitSize());
        }
        
        int bitLength = NukkitMath.bitLength(state);
        if (bitLength > properties.getBitSize()) {
            throw new InvalidBlockStateException(
                    BlockState.of(getBlockId(), state),
                    "The state have more data bits than specified in the properties. Bits: " + bitLength + ", Max: " + properties.getBitSize()
            );
        }

        try {
            BlockProperties properties = this.properties;
            for (String name : properties.getNames()) {
                BlockProperty<?> property = properties.getBlockProperty(name);
                property.validateMeta(state, properties.getOffset(name));
            }
        } catch (InvalidBlockPropertyException e) {
            throw new InvalidBlockStateException(BlockState.of(getBlockId(), state), e);
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setBooleanValue(String propertyName, boolean value) {
        storage = (byte)properties.setBooleanValue(storage, propertyName, value);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setPropertyValue(String propertyName, @Nullable Serializable value) {
        storage = (byte)properties.setValue(storage, propertyName, value);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setIntValue(String propertyName, int value) {
        storage = (byte)properties.setIntValue(storage, propertyName, value);
    }

    @Nonnull
    @Override
    public Serializable getPropertyValue(String propertyName) {
        return properties.getValue(storage, propertyName);
    }

    @Override
    public int getIntValue(String propertyName) {
        return properties.getIntValue(storage, propertyName);
    }

    @Override
    public boolean getBooleanValue(String propertyName) {
        return properties.getBooleanValue(storage, propertyName);
    }

    @Nonnull
    @Override
    public String getPersistenceValue(String propertyName) {
        return properties.getPersistenceValue(storage, propertyName);
    }

    @Nonnull
    @Override
    public BlockState getCurrentState() {
        return BlockState.of(blockId, storage);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getExactIntStorage() {
        return storage;
    }

    @Nonnull
    @Override
    public ByteMutableBlockState copy() {
        return new ByteMutableBlockState(blockId, properties, storage);
    }
}

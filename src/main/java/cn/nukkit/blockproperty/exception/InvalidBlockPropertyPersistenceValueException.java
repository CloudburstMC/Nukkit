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

package cn.nukkit.blockproperty.exception;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;

/**
 * @author joserobjr
 * @since 2020-01-12
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNullableByDefault
public class InvalidBlockPropertyPersistenceValueException extends InvalidBlockPropertyException {
    private static final long serialVersionUID = 1L;
    
    @Nullable
    private final String currentValue;
    
    @Nullable
    private final String invalidValue;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockPropertyPersistenceValueException(@Nonnull BlockProperty<?> property, String currentValue, String invalidValue) {
        super(property, buildMessage(currentValue, invalidValue));
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockPropertyPersistenceValueException(@Nonnull BlockProperty<?> property, String currentValue, String invalidValue, String message) {
        super(property, buildMessage(currentValue, invalidValue)+". "+message);
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockPropertyPersistenceValueException(@Nonnull BlockProperty<?> property, String currentValue, String invalidValue, String message, Throwable cause) {
        super(property, buildMessage(currentValue, invalidValue)+". "+message, cause);
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockPropertyPersistenceValueException(@Nonnull BlockProperty<?> property, String currentValue, String invalidValue, Throwable cause) {
        super(property, buildMessage(currentValue, invalidValue), cause);
        this.currentValue = currentValue;
        this.invalidValue = invalidValue;
    }
    
    private static String buildMessage(Object currentValue, Object invalidValue) {
        return "Current Value: "+currentValue+", Invalid Value: "+invalidValue;
    }


    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public String getCurrentValue() {
        return this.currentValue;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public String getInvalidValue() {
        return this.invalidValue;
    }
}

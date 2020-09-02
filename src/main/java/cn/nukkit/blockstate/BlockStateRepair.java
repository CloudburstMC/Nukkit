package cn.nukkit.blockstate;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyException;
import lombok.Value;
import lombok.experimental.NonFinal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@Value
public class BlockStateRepair {
    /**
     * The block ID of the block state that is being repaired.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    int blockId;

    /**
     * The block properties of the block stat that is being repaired.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    BlockProperties properties;
    
    /**
     * The state that was originally received when the repair started.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    Number originalState;

    /**
     * The current state that is being repaired.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    Number currentState;

    /**
     * The state after the repair. It does not consider {@link #getProposedPropertyValue()}.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    Number nextState;

    /**
     * How many repairs was applied to the original state.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    int repairs;

    /**
     * The property that reported the invalid state, {@code null} if all the properties
     * was validated but the state have more bits to validate.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    BlockProperty<?> property;

    /**
     * The bit position of the invalid property value, when {@link #getProperty()} is {@code null} this indicates
     * the start index of the {@link #getBrokenPropertyMeta()}.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    int propertyOffset;

    /**
     * The current invalid int value that is in the property bit space. 
     * If the {@link #getProperty()} is {@code null} than it will hold all remaining data that can be stored in an integer
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    int brokenPropertyMeta;

    /**
     * The property value that can be set to fix the current block state. It's usually the default property value.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    Serializable fixedPropertyValue;

    /**
     * The proposed property int value to fix the current block state, 
     * if the proposed value is not valid {@link #getFixedPropertyValue()} will be used.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NonFinal
    @Nonnull
    Serializable proposedPropertyValue;

    /**
     * The exception that was thrown when trying to validate the {@link #getCurrentState()} and resulted in this repair.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    InvalidBlockPropertyException validationException;
}

package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.NonNull;
import lombok.experimental.Delegate;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public enum OptionalBoolean {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    TRUE(Boolean.TRUE),
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    FALSE(Boolean.FALSE),

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    EMPTY(null);
    
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Delegate
    @NonNull
    private final Optional<Boolean> optional;

    OptionalBoolean(@Nullable Boolean value) {
        this.optional = Optional.ofNullable(value);
    }

    public static OptionalBoolean of(Boolean value) {
        return of(Objects.requireNonNull(value).booleanValue());
    }
    
    public static OptionalBoolean of(boolean value) {
        return value? TRUE : FALSE;
    }
    
    public static OptionalBoolean ofNullable(Boolean value) {
        return value == null? EMPTY : of(value);
    }
}

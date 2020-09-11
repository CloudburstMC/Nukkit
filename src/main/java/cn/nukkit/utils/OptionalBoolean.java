package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.functional.BooleanConsumer;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

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
    
    @Nullable
    private final Boolean value;

    OptionalBoolean(@Nullable Boolean value) {
        this.value = value;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static OptionalBoolean of(Boolean value) {
        return of(Objects.requireNonNull(value).booleanValue());
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static OptionalBoolean of(boolean value) {
        return value? TRUE : FALSE;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static OptionalBoolean ofNullable(Boolean value) {
        return value == null? EMPTY : of(value);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static OptionalBoolean empty() {
        return EMPTY;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean getAsBoolean() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isPresent() {
        return value != null;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void ifPresent(BooleanConsumer consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean orElse(boolean other) {
        return value != null? value : other;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean orElseGet(BooleanSupplier other) {
        return value != null? value : other.getAsBoolean();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public<X extends Throwable> boolean orElseThrow(Supplier<X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    @Override
    public String toString() {
        return value == null? "OptionalBoolean.empty" : 
                value? "OptionalBoolean[true]" : 
                        "OptionalBoolean[false]";
    }
}

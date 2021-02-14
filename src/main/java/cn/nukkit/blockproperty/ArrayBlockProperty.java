package cn.nukkit.blockproperty;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyPersistenceValueException;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyValueException;
import cn.nukkit.math.NukkitMath;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
public final class ArrayBlockProperty<E extends Serializable> extends BlockProperty<E> {
    private static final long serialVersionUID = 507174531989068430L;
    
    @Nonnull
    private final E[] universe;
    
    private final String[] persistenceNames;
    
    private final Class<E> eClass;
    
    private final boolean ordinal;
    
    private static <E> E[] checkUniverseLength(E[] universe) {
        Preconditions.checkNotNull(universe, "universe can't be null");
        Preconditions.checkArgument(universe.length > 0, "The universe can't be empty");
        return universe;
    }


    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ArrayBlockProperty(String name, boolean exportedToItem, E[] universe, int bitSize, String persistenceName) {
        this(name, exportedToItem, universe, bitSize, persistenceName, false);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ArrayBlockProperty(String name, boolean exportedToItem, E[] universe, int bitSize, String persistenceName, boolean ordinal) {
        this(name, exportedToItem, universe, bitSize, persistenceName,ordinal, ordinal? null : 
                Arrays.stream(universe).map(Objects::toString).map(String::toLowerCase).toArray(String[]::new));
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ArrayBlockProperty(String name, boolean exportedToItem, E[] universe, int bitSize, String persistenceName, boolean ordinal, @Nullable String[] persistenceNames) {
        super(name, exportedToItem, bitSize, persistenceName);
        checkUniverseLength(universe);
        if (!ordinal) {
            Preconditions.checkArgument(persistenceNames != null, "persistenceNames can't be null when ordinal is false");
            Preconditions.checkArgument(persistenceNames.length == universe.length, "persistenceNames and universe must have the same length when ordinal is false");
            this.persistenceNames = persistenceNames.clone();
        } else {
            this.persistenceNames = null;
        }
        this.ordinal = ordinal;
        this.universe = universe.clone();
        //noinspection unchecked
        this.eClass = (Class<E>) universe.getClass().getComponentType();
        Set<E> elements = new HashSet<>();
        Set<String> persistenceNamesCheck = new HashSet<>();
        for (int i = 0; i < this.universe.length; i++) {
            E element = this.universe[i];
            Preconditions.checkNotNull(element, "The universe can not contain null values");
            Preconditions.checkArgument(elements.add(element), "The universe can not have duplicated elements");
            if (!ordinal) {
                String elementName = this.persistenceNames[i];
                Preconditions.checkNotNull(elementName, "The persistenceNames can not contain null values");
                Preconditions.checkArgument(persistenceNamesCheck.add(elementName), "The persistenceNames can not have duplicated elements");
            }
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ArrayBlockProperty(String name, boolean exportedToItem, E[] universe, int bitSize) {
        this(name, exportedToItem, universe, bitSize, name);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ArrayBlockProperty(String name, boolean exportedToItem, E[] universe) {
        this(name, exportedToItem, checkUniverseLength(universe), NukkitMath.bitLength(universe.length - 1));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ArrayBlockProperty(String name, boolean exportedToItem, Class<E> enumClass) {
        this(name, exportedToItem, enumClass.getEnumConstants());
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public ArrayBlockProperty<E> copy() {
        return new ArrayBlockProperty<>(getName(), isExportedToItem(), universe, getBitSize(), getPersistenceName(), isOrdinal(), persistenceNames);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public ArrayBlockProperty<E> exportingToItems(boolean exportedToItem) {
        return new ArrayBlockProperty<>(getName(), exportedToItem, universe, getBitSize(), getPersistenceName(), isOrdinal(), persistenceNames);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public ArrayBlockProperty<E> ordinal(boolean ordinal) {
        if (ordinal == this.ordinal) {
            return this;
        }
        return new ArrayBlockProperty<>(getName(), isExportedToItem(), universe, getBitSize(), getPersistenceName(), ordinal);
    }

    @Override
    public int getMetaForValue(@Nullable E value) {
        if (value == null) {
            return 0;
        }
        for (int i = 0; i < universe.length; i++) {
            if (universe[i].equals(value)) {
                return i;
            }
        }
        throw new InvalidBlockPropertyValueException(this, null, value, "Element is not part of this property");
    }

    @Nonnull
    @Override
    public E getValueForMeta(int meta) {
        return universe[meta];
    }

    @Override
    public int getIntValueForMeta(int meta) {
        try {
            validateMetaDirectly(meta);
        } catch (IllegalArgumentException e) {
            throw new InvalidBlockPropertyMetaException(this, meta, meta, e);
        }
        return meta;
    }
    
    @Nonnull
    @Override
    public String getPersistenceValueForMeta(int meta) {
        try {
            validateMetaDirectly(meta);
        } catch (IllegalArgumentException e) {
            throw new InvalidBlockPropertyMetaException(this, meta, meta, e);
        }
        if (isOrdinal()) {
            return Integer.toString(meta);
        }
        return persistenceNames[meta];
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getMetaForPersistenceValue(String persistenceValue) {
        int meta;
        if (isOrdinal()) {
            try {
                meta = Integer.parseInt(persistenceValue);
                validateMetaDirectly(meta);
            } catch (IndexOutOfBoundsException|IllegalArgumentException e) {
                throw new InvalidBlockPropertyPersistenceValueException(this, null, persistenceValue, 
                        "Expected a number from 0 to " + persistenceNames.length, e);
            }
            return meta;
        }
        for (int index = 0; index < persistenceNames.length; index++) {
            if (persistenceNames[index].equals(persistenceValue)) {
                return index;
            }
        }
        throw new InvalidBlockPropertyPersistenceValueException(
                this, null, persistenceValue,
                "The value does not exists in this property."
        );
    }

    @Override
    protected void validateDirectly(@Nullable E value) {
        for (E object : universe) {
            if (object == value) {
                return;
            }
        }
        throw new IllegalArgumentException(value+" is not valid for this property");
    }

    @Override
    protected void validateMetaDirectly(int meta) {
        Preconditions.checkElementIndex(meta, universe.length);
    }

    @Nonnull
    @Override
    public Class<E> getValueClass() {
        return eClass;
    }

    @Nonnull
    public E[] getUniverse() {
        return universe.clone();
    }

    public boolean isOrdinal() {
        return ordinal;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public E getDefaultValue() {
        return universe[0];
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isDefaultValue(@Nullable E value) {
        return value == null || universe[0].equals(value);
    }
}

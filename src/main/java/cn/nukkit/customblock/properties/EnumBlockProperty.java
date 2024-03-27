package cn.nukkit.customblock.properties;

import cn.nukkit.customblock.properties.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.customblock.properties.exception.InvalidBlockPropertyPersistenceValueException;
import cn.nukkit.customblock.properties.exception.InvalidBlockPropertyValueException;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class EnumBlockProperty<E extends Serializable> extends BlockProperty<E> {
    private static final long serialVersionUID = 507174531989068430L;

    private final E[] values;
    // This is null if ordinal = true
    private final String[] persistenceNames;
    private final Class<E> typeOf;
    private final boolean ordinal;

    
    public EnumBlockProperty(String name, boolean exportedToItem, E[] values, int bitSize, String persistenceName) {
        this(name, exportedToItem, values, bitSize, persistenceName, false);
    }
    
    public EnumBlockProperty(String name, boolean exportedToItem, E[] values, int bitSize, String persistenceName, boolean ordinal) {
        this(name, exportedToItem, values, bitSize, persistenceName, ordinal, ordinal ? null :
                Arrays.stream(values).map(Objects::toString).map(String::toLowerCase).toArray(String[]::new));
    }

    public EnumBlockProperty(String name, boolean exportedToItem, E[] values, int bitSize) {
        this(name, exportedToItem, values, bitSize, name);
    }

    public EnumBlockProperty(String name, boolean exportedToItem, E[] values) {
        this(name, exportedToItem, checkUniverseLength(values), BlockPropertyUtils.bitLength(values.length - 1));
    }

    public EnumBlockProperty(String name, boolean exportedToItem, Class<E> enumClass) {
        this(name, exportedToItem, enumClass.getEnumConstants());
    }

    public EnumBlockProperty(String name, boolean exportedToItem, E[] values, int bitSize, String persistenceName, boolean ordinal, String[] persistenceNames) {
        super(name, exportedToItem, persistenceName, bitSize);
        checkUniverseLength(values);

        if (ordinal) {
            this.persistenceNames = null;
        } else {
            Preconditions.checkArgument(persistenceNames != null, "persistenceNames can't be null when ordinal is false");
            Preconditions.checkArgument(persistenceNames.length == values.length, "persistenceNames and universe must have the same length when ordinal is false");
            this.persistenceNames = persistenceNames.clone();
        }

        this.ordinal = ordinal;
        this.values = values.clone();
        this.typeOf = (Class<E>) values.getClass().getComponentType();

        Set<E> elements = new ObjectOpenHashSet<>();
        Set<String> persistenceNamesCheck = new ObjectOpenHashSet<>();

        for (int i = 0; i < this.values.length; i++) {
            E element = this.values[i];
            Preconditions.checkNotNull(element, "Value can not be null");
            Preconditions.checkArgument(elements.add(element), "Duplicates are not allowed");
            if (!ordinal) {
                String elementName = this.persistenceNames[i];
                Preconditions.checkNotNull(elementName, "The persistenceNames can not contain null values");
                Preconditions.checkArgument(persistenceNamesCheck.add(elementName), "The persistenceNames can not have duplicated elements");
            }
        }
    }

    public EnumBlockProperty<E> ordinal(boolean ordinal) {
        if (ordinal == this.ordinal) {
            return this;
        }
        return new EnumBlockProperty<>(this.getName(), this.isExportedToItem(), this.values, this.getBitSize(), this.getPersistenceName(), ordinal);
    }

    @Override
    public int getMetaForValue( E value) {
        if (value == null) {
            return 0;
        }
        for (int i = 0; i < this.values.length; i++) {
            if (this.values[i].equals(value)) {
                return i;
            }
        }
        throw new InvalidBlockPropertyValueException(this, null, value, "Element is not part of this property");
    }
    
    @Override
    public E getValueForMeta(int meta) {
        return this.values[meta];
    }
    
    @Override
    public int getIntValueForMeta(int meta) {
        try {
            this.validateMetaDirectly(meta);
        } catch (IllegalArgumentException e) {
            throw new InvalidBlockPropertyMetaException(this, meta, meta, e);
        }
        return meta;
    }
    
    @Override
    protected void validateDirectly( E value) {
        for (E object : this.values) {
            if (object == value) {
                return;
            }
        }
        throw new IllegalArgumentException(value+" is not valid for this property");
    }
    
    @Override
    protected void validateMetaDirectly(int meta) {
        Preconditions.checkElementIndex(meta, this.values.length);
    }

    @Override
    public Serializable getPersistenceValueForMeta(int meta) {
        try {
            this.validateMetaDirectly(meta);
        } catch (IllegalArgumentException e) {
            throw new InvalidBlockPropertyMetaException(this, meta, meta, e);
        }

        if (this.isOrdinal()) {
            return meta;
        }
        return persistenceNames[meta];
    }

    @Override
    public int getMetaForPersistenceValue(String persistenceValue) {
        int meta;
        if (this.isOrdinal()) {
            try {
                meta = Integer.parseInt(persistenceValue);
                this.validateMetaDirectly(meta);
            } catch (IndexOutOfBoundsException|IllegalArgumentException e) {
                throw new InvalidBlockPropertyPersistenceValueException(this, null, persistenceValue,
                        "Expected a number from 0 to " + (this.values.length - 1), e);
            }
            return meta;
        }

        for (int index = 0; index < persistenceNames.length; index++) {
            if (persistenceNames[index].equals(persistenceValue)) {
                return index;
            }
        }

        throw new InvalidBlockPropertyPersistenceValueException(this, null, persistenceValue, "The value does not exists in this property.");
    }
    
    public E[] getValues() {
        return this.values.clone();
    }
    
    public boolean isOrdinal() {
        return this.ordinal;
    }
    
    @Override
    public E getDefaultValue() {
        return this.values[0];
    }
    
    @Override
    public boolean isDefaultValue( E value) {
        return value == null || this.values[0].equals(value);
    }

    @Override
    public Class<E> getValueClass() {
        return this.typeOf;
    }

    @Override
    public EnumBlockProperty<E> copy() {
        return new EnumBlockProperty<>(this.getName(), this.isExportedToItem(), values, this.getBitSize(), this.getPersistenceName(), this.isOrdinal(), persistenceNames);
    }

    @Override
    public EnumBlockProperty<E> exportingToItems(boolean exportedToItem) {
        return new EnumBlockProperty<>(this.getName(), exportedToItem, values, this.getBitSize(), this.getPersistenceName(), this.isOrdinal(), persistenceNames);
    }

    private static <E> E[] checkUniverseLength(E[] universe) {
        Preconditions.checkNotNull(universe, "universe can't be null");
        Preconditions.checkArgument(universe.length > 0, "The universe can't be empty");
        return universe;
    }
}

package cn.nukkit.blockproperty;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public final class ArrayBlockProperty<E> extends BlockProperty<E> {
    @Nonnull
    private final E[] universe;
    
    private final int defaultMeta;
    
    private static <E> E[] checkUniverseLength(@Nonnull E[] universe) {
        Preconditions.checkArgument(universe.length > 0, "The universe can't be empty");
        return universe;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ArrayBlockProperty(String name, @Nonnull E[] universe, @Nonnull E defaultValue, int bitSize, String persistenceName) {
        super(name, bitSize, persistenceName);
        this.universe = universe.clone();
        checkUniverseLength(universe);
        Set<E> elements = new HashSet<>();
        int defaultMetaIndex = -1;
        for (int i = 0; i < this.universe.length; i++) {
            E element = this.universe[i];
            Preconditions.checkNotNull(element, "The universe can not contain null values");
            Preconditions.checkArgument(!elements.add(element), "The universe can not have duplicated elements");
            if (element.equals(defaultValue)) {
                defaultMetaIndex = i;
            }
        }
        
        Preconditions.checkArgument(defaultMetaIndex >= 0, "The universe must contain the default value instance");
        this.defaultMeta = defaultMetaIndex;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ArrayBlockProperty(String name, @Nonnull E[] universe, @Nonnull E defaultValue, int bitSize) {
        this(name, universe, defaultValue, bitSize, name);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ArrayBlockProperty(String name, @Nonnull E[] universe, @Nonnull E defaultValue) {
        this(name, universe, defaultValue, calculateBitSize(universe.length - 1));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ArrayBlockProperty(String name, @Nonnull E[] universe) {
        this(name, checkUniverseLength(universe), universe[0]);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ArrayBlockProperty(String name, @Nonnull Class<E> enumClass) {
        this(name, enumClass.getEnumConstants());
    }

    @Override
    public int getMetaForValue(@Nullable E value) {
        if (value == null) {
            return defaultMeta;
        }
        for (int i = 0; i < universe.length; i++) {
            if (universe[i].equals(value)) {
                return i;
            }
        }
        throw new IllegalArgumentException(value+" is not valid for this property");
    }

    @Nonnull
    @Override
    public E getValueForMeta(int meta) {
        return universe[meta];
    }

    @Override
    public int getIntValueForMeta(int meta) {
        return meta;
    }

    @Override
    public String getPersistenceValueForMeta(int meta) {
        return getValueForMeta(meta).toString().toLowerCase();
    }

    @Override
    public void validate(@Nullable E value) {
        for (E object : universe) {
            if (object == value) {
                return;
            }
        }
        throw new IllegalArgumentException(value+" is not valid for this property");
    }

    @Override
    public void validateMeta(int meta) {
        Preconditions.checkElementIndex(meta, universe.length);
    }
}

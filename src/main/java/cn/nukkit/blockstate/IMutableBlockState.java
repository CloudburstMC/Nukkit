package cn.nukkit.blockstate;

import cn.nukkit.blockproperty.BlockProperty;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface IMutableBlockState extends IBlockState {
    void setDataStorage(Number storage);

    void setDataStorageFromInt(int storage);

    void setPropertyValue(String propertyName, @Nullable Object value);

    default <T> void setPropertyValue(BlockProperty<T> property, @Nullable T value) {
        setPropertyValue(property.getName(), value);
    }
    
    default boolean toggleBooleanProperty(String propertyName) {
        boolean newValue = !getBooleanValue(propertyName);
        setPropertyValue(propertyName, newValue);
        return newValue;
    }
    
    default boolean toggleBooleanProperty(BlockProperty<Boolean> property) {
        return toggleBooleanProperty(property.getName());
    }
}

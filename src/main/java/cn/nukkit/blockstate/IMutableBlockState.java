package cn.nukkit.blockstate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IMutableBlockState extends IBlockState {
    void setDataStorage(@Nonnull Number storage);

    void setDataStorageFromInt(int storage);

    void setPropertyValue(@Nonnull String propertyName, @Nullable Object value);
}

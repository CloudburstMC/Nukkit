package cn.nukkit.blockstate;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockHyperMeta;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.level.GlobalBlockPalette;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

@ToString
@EqualsAndHashCode
@ParametersAreNonnullByDefault
public abstract class BlockState {
    protected final int blockId;
    protected final BlockProperties properties;
    

    BlockState(int blockId, BlockProperties properties) {
        this.blockId = blockId;
        this.properties = properties;
    }
    
    public abstract void validate();

    public final int getBlockId() {
        return blockId;
    }

    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "the BlockState itself")
    public final int getFullId() {
        return (blockId << Block.DATA_BITS) | (getLegacyDamage() & Block.DATA_MASK);
    }
    
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    public abstract int getLegacyDamage();

    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    public abstract int getHyperDamage();

    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "the BlockState itself")
    public final long getHyperId() {
        return ((long)blockId << BlockHyperMeta.HYPER_DATA_BITS) | (getHyperDamage() & BlockHyperMeta.HYPER_DATA_MASK);
    }
    
    public abstract Number getDataStorage();
    
    public abstract void setDataStorage(Number storage);
    
    public abstract void setDataStorageFromInt(int storage);

    public final BlockProperties getProperties() {
        return properties;
    }

    @SuppressWarnings("rawtypes")
    public BlockProperty getProperty(String propertyName) {
        return properties.getBlockProperty(propertyName);
    }

    public <T extends BlockProperty<?>> T getCheckedProperty(String propertyName, Class<T> tClass) {
        return properties.getBlockProperty(propertyName, tClass);
    }

    public Collection<String> getPropertyNames() {
        return properties.getNames();
    }

    public abstract void setPropertyValue(String propertyName, Object value);

    @Nonnull
    public abstract Object getPropertyValue(String propertyName);

    public final <T> T getCheckedPropertyValue(String propertyName, Class<T> tClass) {
        return tClass.cast(getPropertyValue(propertyName));
    }

    @SuppressWarnings("unchecked")
    public <T> T getUncheckedPropertyValue(String propertyName) {
        return (T) getPropertyValue(propertyName);
    }

    public abstract int getIntValue(String propertyName);

    public abstract boolean getBooleanValue(String propertyName);
    
    public abstract String getPersistenceValue(String propertyName);
    
    public abstract BlockState copy();

    public int getRuntimeId() {
        return GlobalBlockPalette.getOrCreateRuntimeId(getBlockId(), getHyperDamage());
    }
}
